package com.jigsaw.core.manager;

import com.jigsaw.commons.exeption.JigsawAssemblyException;
import com.jigsaw.commons.exeption.JigsawConnectException;
import com.jigsaw.commons.exeption.JigsawDisconnectException;
import com.jigsaw.core.util.JigsawClassLoader;
import com.jigsaw.commons.model.JigsawPiece;
import com.jigsaw.commons.model.SimpleJigsawPiece;
import com.jigsaw.core.util.ResourceLoader;
import com.jigsaw.core.util.ResourceLoaderFactory;
import com.jigsaw.core.util.JarUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;

public class JigsawPieceManager {

    private static final Logger log = LoggerFactory.getLogger(JigsawPieceManager.class);

    private String localRepository = "C:\\projects\\out";

    private String[] remoteRepositories = new String[]{};

    private ResourceLoader resourceLoader = ResourceLoaderFactory.getInstance();

    private Map<String, JigsawPiece> pieces = new LinkedHashMap<String, JigsawPiece>();

    @Autowired
    private ClassLoaderManager classLoaderManager;

    public void init() {
        classLoaderManager.addResource("com.jigsaw.commons.model", this.getClass().getClassLoader());
        classLoaderManager.addResource("com.jigsaw.commons.exception", this.getClass().getClassLoader());
    }

    public void destroy() {
        classLoaderManager.removeResource("com.jigsaw.commons.model", this.getClass().getClassLoader());
        classLoaderManager.removeResource("com.jigsaw.commons.exception", this.getClass().getClassLoader());
    }

    public boolean hasPiece(String pieceId) {
        return pieces.containsKey(pieceId);
    }

    public JigsawPiece getPiece(String pieceId) {
        return pieces.get(pieceId);
    }

    public Collection<JigsawPiece> getPieces() {
        return pieces.values();
    }

    /**
     * - First, all dependencies are connected if not already connected <br/>
     * - Lastly, the piece itself is connected if not already connected
     *
     * @param jigsawPiece the piece being connected
     * @return list of all connected pieces
     * @throws JigsawConnectException
     */
    public Set<JigsawPiece> connectPiece(JigsawPiece jigsawPiece) throws JigsawConnectException {
        Set<JigsawPiece> connectedPieces = new HashSet<JigsawPiece>();

        if (jigsawPiece.getStatus() == SimpleJigsawPiece.Status.CONNECTED) {
            return connectedPieces;
        }

        connectInternal(jigsawPiece, connectedPieces);

        return connectedPieces;
    }

    protected void connectInternal(JigsawPiece jigsawPiece, Set<JigsawPiece> connectedPieces)
            throws JigsawConnectException {
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

                connectedPieces.add(jigsawPiece);
            }
        } catch (Exception e) {
            throw new JigsawConnectException("Unable to connect the jigsaw piece", e);
        }
    }

    /**
     * Disconnects a given piece plus all dependencies that have no more
     * dependents. <br/>
     *
     * @param jigsawPiece the piece to disconnect
     * @return all the pieces that have been disconnected
     * @throws JigsawDisconnectException pieces that still have dependants
     *                                   cannot be disconnected
     */
    public Set<JigsawPiece> disconnectPiece(JigsawPiece jigsawPiece) throws JigsawDisconnectException {
        if (!jigsawPiece.getDependants().isEmpty()) {
            throw new JigsawDisconnectException("Cannot disconnect piece with dependant pieces");
        }

        Set<JigsawPiece> disconnectedPieces = new HashSet<JigsawPiece>();

        if (jigsawPiece.getStatus() == SimpleJigsawPiece.Status.DISCONNECTED) {
            return disconnectedPieces;
        }

        disconnectInternal(jigsawPiece, disconnectedPieces);

        return disconnectedPieces;
    }

    protected void disconnectInternal(JigsawPiece jigsawPiece, Set<JigsawPiece> disconnectedPieces) {
        try {
            if (jigsawPiece.getStatus() == SimpleJigsawPiece.Status.CONNECTED) {
                jigsawPiece.setStatus(SimpleJigsawPiece.Status.DISCONNECTED);

                resourceLoader.loadResources(jigsawPiece);

                if (jigsawPiece.getConnector() != null) {
                    jigsawPiece.getConnector().disconnect(jigsawPiece);
                }
            }

            disconnectedPieces.add(jigsawPiece);

            for (String dependencyId : jigsawPiece.getDependencies()) {
                JigsawPiece dependency = getPiece(dependencyId);

                dependency.getDependants().remove(jigsawPiece.getId());
                if (dependency.getDependants().isEmpty()) {
                    disconnectInternal(dependency, disconnectedPieces);
                }

                disconnectInternal(dependency, disconnectedPieces);
            }

        } catch (Exception e) {
            throw new JigsawConnectException("Unable to connect the jigsaw piece", e);
        }
    }

    /**
     * Removes a given piece plus all dependencies that have no more
     * dependents. <br/>
     *
     * @param jigsawPiece the piece to remove
     * @return all the pieces that have been removed
     * @throws JigsawDisconnectException pieces that still have dependants
     *                                   cannot be removed
     */
    public Set<JigsawPiece> removePiece(JigsawPiece jigsawPiece) {
        Set<JigsawPiece> disconnectedPieces = disconnectPiece(jigsawPiece);
        for (JigsawPiece disconnectedPiece : disconnectedPieces) {
            classLoaderManager.removeClassLoader((JigsawClassLoader) disconnectedPiece.getClassLoader());

            pieces.remove(disconnectedPiece.getId());
        }

        return disconnectedPieces;
    }

    /**
     * - First, the old piece is removed <br/>
     * - Second, the new piece and its new dependencies are added <br/>
     *
     * @param pieceId    the old piece id to replace
     * @param groupId    new group id
     * @param artifactId new artifact id
     * @param version    new version
     * @return the new piece
     */
    public JigsawPiece replacePiece(String pieceId, String groupId, String artifactId, String version) {
        JigsawPiece oldPiece = getPiece(pieceId);
        if (oldPiece != null) {
            removePiece(oldPiece);
        }

        return addPiece(groupId, artifactId, version);
    }

    /**
     * - First, the piece is fetched from either a local or remote repository <br/>
     * - Second, the piece and all of its dependencies are added to the system.
     * If any of the pieces being added already exist, they won't be added. <br/>
     *
     * @param groupId    group id
     * @param artifactId artifact id
     * @param version    version
     * @return the added piece
     */
    public JigsawPiece addPiece(String groupId, String artifactId, String version) {
        try {
            RepositorySystem repoSystem = newRepositorySystem();

            RepositorySystemSession session = newSession(repoSystem);

            Dependency dependency = new Dependency(new org.eclipse.aether.artifact.DefaultArtifact(groupId, artifactId, "jar", version), "compile");

            RemoteRepository central = new RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/").build();

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(dependency);
            //collectRequest.addRepository(central);
            for (int i = 0; i < remoteRepositories.length; i++) {
                String remoteRepo = remoteRepositories[i];

                RemoteRepository repo = new RemoteRepository.Builder("repo-" + i, "default", remoteRepo).build();

                collectRequest.addRepository(repo);
            }

            DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();

            DependencyRequest dependencyRequest = new DependencyRequest();
            dependencyRequest.setRoot(node);
            dependencyRequest.setFilter(new ScopeDependencyFilter(new String[]{"provided", "test"}));

            DependencyResult dependencyResult = repoSystem.resolveDependencies(session, dependencyRequest);

            return addPiece(null, dependencyResult.getRoot());

        } catch (DependencyCollectionException e) {
            throw new JigsawAssemblyException("Unable to add a new JigsawPiece", e);
        } catch (DependencyResolutionException e) {
            throw new JigsawAssemblyException("Unable to add a new JigsawPiece", e);
        }
    }

    protected JigsawPiece addPiece(String dependantId, DependencyNode root) {
        String rootId = generateId(root.getArtifact());
        if (hasPiece(rootId)) {
            return getPiece(rootId);
        }

        JigsawPiece jigsawPiece = convert(root.getArtifact());

        JigsawClassLoader classLoader = classLoaderManager.addClassLoader(jigsawPiece, null);

        jigsawPiece.setClassLoader(classLoader);

        pieces.put(rootId, jigsawPiece);

        if (StringUtils.isNotEmpty(dependantId)) {
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
        locator.addService(org.eclipse.aether.spi.connector.RepositoryConnectorFactory.class,
                BasicRepositoryConnectorFactory.class);
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
        jigsawPiece.setId(generateId(artifact));
        jigsawPiece.setArtifactId(artifact.getArtifactId());
        jigsawPiece.setGroupId(artifact.getGroupId());
        jigsawPiece.setVersion(artifact.getVersion());
        jigsawPiece.setFile(artifact.getFile());

        return jigsawPiece;
    }

    protected String generateId(Artifact artifact) {
        try {
            StringBuffer sb = new StringBuffer()
                    .append(artifact.getGroupId())
                    .append(":")
                    .append(artifact.getArtifactId())
                    .append(":")
                    .append(artifact.getVersion())
                    .append(":")
                    .append(JarUtils.getChecksum(artifact.getFile()));

            return sb.toString();

        } catch (IOException e) {
            throw new JigsawAssemblyException("Unable to generate an id for the artifact", e);
        }
    }

    public void setLocalRepository(String localRepository) {
        this.localRepository = localRepository;
    }

    public void setRemoteRepositories(String remoteRepositories) {
        this.remoteRepositories = remoteRepositories.split(",");
    }

    public ClassLoaderManager getClassLoaderManager() {
        return classLoaderManager;
    }
}
