package com.jigsaw.manager;

import com.jigsaw.model.JigsawPiece;
import com.jigsaw.model.SimpleJigsawPiece;
import com.jigsaw.util.JarUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Set;

/**
 * Created by RH on 3/28/2016.
 */
public class JigsawPieceManagerTest {

    private String testChecksum;

    private String testId;

    private String testDependencyChecksum;

    private String testDependencyId;

    @Before
    public void before() throws Exception {
        testChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test/1.0.0-SNAPSHOT/jigsaw-test-1.0.0-SNAPSHOT.jar").getFile()));
        testId = "com.jigsaw:jigsaw-test:1.0.0-SNAPSHOT:" + testChecksum;

        testDependencyChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test-dependency/1.0.0-SNAPSHOT/jigsaw-test-dependency-1.0.0-SNAPSHOT.jar").getFile()));
        testDependencyId = "com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT:" + testDependencyChecksum;
    }

    @Test
    public void testAddPiece() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        Assert.assertTrue(piece.getDependencies().contains(testDependencyId));
        Assert.assertNotNull(piece.getClassLoader());
        Assert.assertNotNull(piece.getFile());
        Assert.assertNotNull(pieceManager.getPiece(piece.getId()));
        Assert.assertNotNull(pieceManager.getPiece(testDependencyId));
    }

    @Test
    public void testConnectPiece() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");
        JigsawPiece dependency = pieceManager.getPiece(testDependencyId);

        Set<JigsawPiece> pieces = pieceManager.connectPiece(piece);

        Assert.assertEquals(2, pieces.size());
        Assert.assertTrue(pieces.contains(piece));
        Assert.assertTrue(pieces.contains(dependency));
        Assert.assertEquals(SimpleJigsawPiece.Status.CONNECTED, piece.getStatus());
        Assert.assertEquals(SimpleJigsawPiece.Status.CONNECTED, dependency.getStatus());
    }

    @Test
    public void testDisconnectPiece() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");
        JigsawPiece dependency = pieceManager.getPiece(testDependencyId);

        pieceManager.connectPiece(piece);

        Set<JigsawPiece> pieces = pieceManager.disconnectPiece(piece);

        Assert.assertEquals(2, pieces.size());
        Assert.assertTrue(pieces.contains(piece));
        Assert.assertTrue(pieces.contains(dependency));
        Assert.assertEquals(SimpleJigsawPiece.Status.DISCONNECTED, piece.getStatus());
        Assert.assertEquals(SimpleJigsawPiece.Status.DISCONNECTED, dependency.getStatus());
    }

    @Test
    public void testRemove() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");
        JigsawPiece dependency = pieceManager.getPiece(testDependencyId);

        pieceManager.connectPiece(piece);

        Set<JigsawPiece> pieces = pieceManager.removePiece(piece);

        Assert.assertTrue(pieceManager.getPieces().isEmpty());
    }
}
