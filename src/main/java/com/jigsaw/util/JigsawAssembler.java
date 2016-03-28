package com.jigsaw.util;

import com.jigsaw.exeption.JigsawAssemblyException;
import com.jigsaw.exeption.JigsawConnectException;
import com.jigsaw.model.JigsawClassLoader;
import com.jigsaw.model.JigsawPiece;
import com.jigsaw.model.SimpleJigsawPiece;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.filter.ScopeDependencyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.*;

public class JigsawAssembler
{
  public JigsawAssembler() {}
  
  private static final Logger log = LoggerFactory.getLogger(JigsawAssembler.class);
  
  private String localRepository = "C:\\projects\\out";
  
  private ResourceLoader resourceLoader = ResourceLoaderFactory.getInstance();
  
  private ClassLoaderProvider classLoaderProvider = ClassLoaderProviderFactory.getInstance();
  
  private Map<String, JigsawPiece> pieces = new LinkedHashMap();
  
  public boolean hasPiece(String pieceId) {
    return pieces.containsKey(pieceId);
  }
  
  public JigsawPiece getPiece(String pieceId) {
    return (JigsawPiece)pieces.get(pieceId);
  }
  
  public Collection<JigsawPiece> getPieces() {
    return pieces.values();
  }
  
  public void assemble() {
    for (JigsawPiece piece : pieces.values()) {
      connect(piece);
    }
  }
  






  public Set<JigsawPiece> connect(JigsawPiece jigsawPiece)
    throws JigsawConnectException
  {
    Set<JigsawPiece> connectedPieces = new HashSet();
    
    if (jigsawPiece.getStatus() == SimpleJigsawPiece.Status.CONNECTED) {
      return connectedPieces;
    }
    
    connectInternal(jigsawPiece, connectedPieces);
    
    return connectedPieces;
  }
  
  protected void connectInternal(JigsawPiece jigsawPiece, Set<JigsawPiece> connectedPieces) throws JigsawConnectException
  {
    try {
      for (String dependencyId : jigsawPiece.getDependencies()) {
        connectInternal(getPiece(dependencyId), connectedPieces);
      }
      
      if (jigsawPiece.getStatus() != SimpleJigsawPiece.Status.CONNECTED) {
        jigsawPiece.setStatus(SimpleJigsawPiece.Status.CONNECTED);
        
        resourceLoader.loadResources(jigsawPiece);
        
        if (jigsawPiece.getConnector() != null) {
          jigsawPiece.getConnector().connect(jigsawPiece);
        }
      }
      
      connectedPieces.add(jigsawPiece);
    }
    catch (Exception e) {
      throw new JigsawConnectException("Unable to connect the jigsaw piece", e);
    }
  }
  








  public Set<JigsawPiece> disconnect(JigsawPiece jigsawPiece)
  {
    Set<JigsawPiece> disconnectedPieces = new HashSet();
    
    if (jigsawPiece.getStatus() == SimpleJigsawPiece.Status.DISCONNECTED) {
      return disconnectedPieces;
    }
    
    disconnectInternal(jigsawPiece, disconnectedPieces);
    
    return disconnectedPieces;
  }
  
  protected void disconnectInternal(JigsawPiece jigsawPiece, Set<JigsawPiece> disconnectedPieces) {
    try {
      for (String dependantId : jigsawPiece.getDependants()) {
        disconnectInternal(getPiece(dependantId), disconnectedPieces);
      }
      
      for (String dependencyId : jigsawPiece.getDependencies()) {
        JigsawPiece dependency = getPiece(dependencyId);
        if ((dependency.getDependants().size() <= 1) && (dependency.getDependants().contains(jigsawPiece.getId()))) {
          disconnectInternal(dependency, disconnectedPieces);
        }
        
        disconnectInternal(dependency, disconnectedPieces);
      }
      
      if (jigsawPiece.getStatus() == SimpleJigsawPiece.Status.CONNECTED) {
        jigsawPiece.setStatus(SimpleJigsawPiece.Status.DISCONNECTED);
        
        resourceLoader.loadResources(jigsawPiece);
        
        if (jigsawPiece.getConnector() != null) {
          jigsawPiece.getConnector().disconnect(jigsawPiece);
        }
      }
      
      disconnectedPieces.add(jigsawPiece);
    }
    catch (Exception e) {
      throw new JigsawConnectException("Unable to connect the jigsaw piece", e);
    }
  }
  




  public void removePiece(JigsawPiece piece)
  {
    Set<JigsawPiece> disconnectedPieces = disconnect(piece);
    for (JigsawPiece disconnectedPiece : disconnectedPieces) {
      pieces.remove(disconnectedPiece.getId());
      
      classLoaderProvider.removeClassLoader(disconnectedPiece);
    }
  }
  
  public JigsawPiece addPiece(String groupId, String artifactId, String version) {
    try {
      RepositorySystem repoSystem = newRepositorySystem();
      
      RepositorySystemSession session = newSession(repoSystem);
      
      Dependency dependency = new Dependency(new org.eclipse.aether.artifact.DefaultArtifact(groupId, artifactId, "jar", version), "compile");
      
      RemoteRepository central = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/").build();
      

      CollectRequest collectRequest = new CollectRequest();
      collectRequest.setRoot(dependency);
      collectRequest.addRepository(central);
      DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
      
      DependencyRequest dependencyRequest = new DependencyRequest();
      dependencyRequest.setRoot(node);
      dependencyRequest.setFilter(new ScopeDependencyFilter(new String[] { "provided", "test" }));
      
      DependencyResult dependencyResult = repoSystem.resolveDependencies(session, dependencyRequest);
      
      return addPiece(null, dependencyResult.getRoot());
    }
    catch (DependencyCollectionException e) {
      throw new JigsawAssemblyException("Unable to add a new JigsawPiece", e);
    }
    catch (DependencyResolutionException e) {
      throw new JigsawAssemblyException("Unable to add a new JigsawPiece", e);
    }
  }
  
  protected JigsawPiece addPiece(String dependantId, DependencyNode root) {
    String rootId = generateId(root.getArtifact().getGroupId(), root.getArtifact().getArtifactId(), root.getArtifact().getVersion());
    

    JigsawPiece jigsawPiece = getPiece(rootId);
    if (jigsawPiece == null) {
      jigsawPiece = convert(root.getArtifact());
      
      JigsawClassLoader classLoader = new JigsawClassLoader(jigsawPiece, getClass().getClassLoader());
      

      classLoaderProvider.addClassLoader(classLoader);
      
      jigsawPiece.setClassLoader(classLoader);
      
      pieces.put(rootId, jigsawPiece);
    }
    
    if (org.apache.commons.lang3.StringUtils.isNotEmpty(dependantId)) {
      jigsawPiece.getDependants().add(dependantId);
    }
    
    for (DependencyNode dependencyNode : root.getChildren()) {
      JigsawPiece dependency = addPiece(rootId, dependencyNode);
      
      jigsawPiece.getDependencies().add(dependency.getId());
    }
    
    return jigsawPiece;
  }
  
  private RepositorySystem newRepositorySystem() {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(org.eclipse.aether.spi.connector.RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    
    return locator.getService(RepositorySystem.class);
  }
  
  private RepositorySystemSession newSession(RepositorySystem system) {
    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    
    LocalRepository localRepo = new LocalRepository(localRepository);
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
    
    return session;
  }
  
  protected JigsawPiece convert(Artifact artifact) {
    JigsawPiece jigsawPiece = new JigsawPiece();
    jigsawPiece.setId(generateId(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion()));
    jigsawPiece.setArtifactId(artifact.getArtifactId());
    jigsawPiece.setGroupId(artifact.getGroupId());
    jigsawPiece.setVersion(artifact.getVersion());
    try {
      jigsawPiece.setUrl(artifact.getFile().toURI().toURL());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    
    return jigsawPiece;
  }
  
  protected String generateId(String groupId, String artifactId, String version) {
    return groupId + ":" + artifactId + ":" + version;
  }
  




























































  public void setLocalRepository(String localRepository)
  {
    this.localRepository = localRepository;
  }
}
