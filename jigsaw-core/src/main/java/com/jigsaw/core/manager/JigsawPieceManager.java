package com.jigsaw.core.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jigsaw.core.converter.ArtifactToJigsawPieceConverter;
import com.jigsaw.core.exeption.JigsawAssemblyException;
import com.jigsaw.core.exeption.JigsawConnectException;
import com.jigsaw.core.exeption.JigsawDisconnectException;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import com.jigsaw.core.model.SimpleJigsawPiece;
import com.jigsaw.core.util.JarUtils;
import com.jigsaw.core.util.ResourceLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
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

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

/**
 * Manages all the jigsaw pieces and their state
 *
 * @author Ramtin Hosseini
 */
public class JigsawPieceManager {

    private static final Logger log = LoggerFactory.getLogger(JigsawPieceManager.class);

    private String DB_FILE = "jigsaw-db.json";

    private String localRepository = "C:\\projects\\out";

    private String[] remoteRepositories = new String[]{};

    private ResourceLoader resourceLoader;

    private Map<String, JigsawPiece> pieces = new HashMap<>();

    private ClassLoaderManager classLoaderManager;

    private ArtifactToJigsawPieceConverter artifactToJigsawPieceConverter;

    public void init() {
        classLoaderManager.addResource("com.jigsaw.core", this.getClass().getClassLoader());
        classLoaderManager.addResource("com.jigsaw.core.model", this.getClass().getClassLoader());
        classLoaderManager.addResource("com.jigsaw.core.manager", this.getClass().getClassLoader());
        classLoaderManager.addResource("com.jigsaw.core.util", this.getClass().getClassLoader());
        classLoaderManager.addResource("com.jigsaw.core.converter", this.getClass().getClassLoader());
        classLoaderManager.addResource("com.jigsaw.core.exception", this.getClass().getClassLoader());
    }

    public void destroy() {
        classLoaderManager.removeResource("com.jigsaw.core", this.getClass().getClassLoader());
        classLoaderManager.removeResource("com.jigsaw.core.model", this.getClass().getClassLoader());
        classLoaderManager.removeResource("com.jigsaw.core.manager", this.getClass().getClassLoader());
        classLoaderManager.removeResource("com.jigsaw.core.util", this.getClass().getClassLoader());
        classLoaderManager.removeResource("com.jigsaw.core.converter", this.getClass().getClassLoader());
        classLoaderManager.removeResource("com.jigsaw.core.exception", this.getClass().getClassLoader());
    }

    public List<SimpleJigsawPiece> getPersistedPieces() {
        Reader reader = null;
        try {
            URL dbUrl = getClass().getResource(getDbFilePath());
            if (dbUrl == null) {
                log.warn("No db file found at " + getDbFilePath());

                return new ArrayList();
            }

            reader = new FileReader(dbUrl.getFile());

            Type listType = new TypeToken<ArrayList<SimpleJigsawPiece>>() {
            }.getType();

            Gson gson = new GsonBuilder().create();
            List<SimpleJigsawPiece> dbPieces = gson.fromJson(reader, listType);

            return dbPieces;

        } catch (IOException e) {
            log.error("Unable to read jigsaw pieces from db file", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException e) {
                log.error("Unable to close db file", e);
            }
        }

        return new ArrayList();
    }

    public void persistPieces() {
        Writer writer = null;
        try {
            writer = new FileWriter(new File(getDbFilePath()));

            Gson gson = new GsonBuilder().create();
            gson.toJson(getPieces(), writer);

        } catch (IOException e) {
            log.error("Unable to write jigsaw pieces to db file", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

            } catch (IOException e) {
                log.error("Unable to close db file", e);
            }
        }
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

    public Map<String, JigsawPiece> getPiecesMap() {
        return pieces;
    }

    /**
     * - First, all dependencies are connected<br/>
     * - Lastly, the piece itself is connected
     *
     * @param jigsawPiece the piece being connected
     * @return list of all connected pieces
     * @throws JigsawConnectException
     */
    public Set<JigsawPiece> connectPiece(JigsawPiece jigsawPiece) throws JigsawConnectException {
        Set<JigsawPiece> connectedPieces = new HashSet<JigsawPiece>();

        if (jigsawPiece.getStatus() == JigsawPieceStatus.CONNECTED) {
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

            if (jigsawPiece.getStatus() != JigsawPieceStatus.CONNECTED) {
                jigsawPiece.setStatus(JigsawPieceStatus.CONNECTED);

                resourceLoader.loadResources(jigsawPiece);

                if (jigsawPiece.getConnector() != null) {
                    jigsawPiece.getConnector().connect(jigsawPiece);
                }

                connectedPieces.add(jigsawPiece);

                log.info("Connected piece " + jigsawPiece.getId());
            }

        } catch (Exception e) {
            throw new JigsawConnectException("Unable to connect the jigsaw piece", e);
        }
    }

    /**
     * - First, the piece itself is disconnected <br/>
     * - Lastly, all dependencies that no longer have any dependents
     * are disconnected
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

        if (jigsawPiece.getStatus() == JigsawPieceStatus.DISCONNECTED) {
            return disconnectedPieces;
        }

        disconnectInternal(jigsawPiece, disconnectedPieces);

        return disconnectedPieces;
    }

    protected void disconnectInternal(JigsawPiece jigsawPiece, Set<JigsawPiece> disconnectedPieces) {
        try {
            if (jigsawPiece.getStatus() == JigsawPieceStatus.CONNECTED) {
                jigsawPiece.setStatus(JigsawPieceStatus.DISCONNECTED);

                if (jigsawPiece.getConnector() != null) {
                    jigsawPiece.getConnector().disconnect(jigsawPiece);
                }

                disconnectedPieces.add(jigsawPiece);

                log.info("Disconnected piece " + jigsawPiece.getId());
            }

            for (String dependencyId : jigsawPiece.getDependencies()) {
                JigsawPiece dependency = getPiece(dependencyId);

                if (dependency.getDependants().size() == 1) {
                    disconnectInternal(dependency, disconnectedPieces);
                }
            }

        } catch (Exception e) {
            throw new JigsawConnectException("Unable to connect the jigsaw piece", e);
        }
    }

    /**
     * - First, the piece itself is removed <br/>
     * - Lastly, all dependencies that no longer have any dependents
     * are removed
     *
     * @param jigsawPiece the piece to remove
     * @return all the pieces that have been removed
     * @throws JigsawDisconnectException pieces that still have dependants
     *                                   cannot be removed
     */
    public void removePiece(JigsawPiece jigsawPiece) {
        if (!jigsawPiece.getDependants().isEmpty()) {
            throw new JigsawDisconnectException("Cannot remove piece with dependant pieces");
        }

        if (jigsawPiece.getStatus() == JigsawPieceStatus.CONNECTED) {
            throw new JigsawDisconnectException("Cannot remove a piece that is still connected");
        }

        classLoaderManager.removeClassLoader((ClassLoaderManager.JigsawClassLoader) jigsawPiece.getClassLoader());

        pieces.remove(jigsawPiece.getId());

        log.info("Removed piece " + jigsawPiece.getId());

        for (String dependencyId : jigsawPiece.getDependencies()) {
            JigsawPiece dependency = getPiece(dependencyId);

            dependency.getDependants().remove(jigsawPiece.getId());

            if (dependency.getDependants().isEmpty() &&
                    dependency.getStatus() != JigsawPieceStatus.CONNECTED) {
                removePiece(dependency);
            }
        }
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
        String rootId = JarUtils.generateId(root.getArtifact());
        if (hasPiece(rootId)) {
            return getPiece(rootId);
        }

        JigsawPiece jigsawPiece = artifactToJigsawPieceConverter.convert(root.getArtifact());

        ClassLoaderManager.JigsawClassLoader classLoader = classLoaderManager.addClassLoader(jigsawPiece, null);

        jigsawPiece.setClassLoader(classLoader);

        pieces.put(rootId, jigsawPiece);

        log.info("Added piece " + jigsawPiece.getId());

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

        URL localPath = getClass().getResource(localRepository);
        String path;
        if (localPath == null) {
            path = localRepository;
        } else {
            path = localPath.getFile();
        }

        log.info("Setting local repository to " + path);

        LocalRepository localRepo = new LocalRepository(path);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
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

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setClassLoaderManager(ClassLoaderManager classLoaderManager) {
        this.classLoaderManager = classLoaderManager;
    }

    public void setArtifactToJigsawPieceConverter(ArtifactToJigsawPieceConverter artifactToJigsawPieceConverter) {
        this.artifactToJigsawPieceConverter = artifactToJigsawPieceConverter;
    }

    protected String getDbFilePath() {
        if (localRepository.endsWith("/")) {
            return localRepository + DB_FILE;
        } else {
            return localRepository + "/" + DB_FILE;
        }
    }
}
