package com.jigsaw.core.manager;

import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import com.jigsaw.core.model.SimpleJigsawPiece;
import com.jigsaw.core.util.JarUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by RH on 3/28/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/jigsaw-test-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JigsawPieceManagerTest {

    private String testChecksum;

    private String testId;

    private String testDependencyChecksum;

    private String testDependencyId;

    @Autowired
    private JigsawPieceManager pieceManager;

    @Before
    public void before() throws Exception {
        testChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test/1.0.0-SNAPSHOT/jigsaw-test-1.0.0-SNAPSHOT.jar").getFile()));
        testId = "com.jigsaw:jigsaw-test:1.0.0-SNAPSHOT:" + testChecksum;

        testDependencyChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test-dependency/1.0.0-SNAPSHOT/jigsaw-test-dependency-1.0.0-SNAPSHOT.jar").getFile()));
        testDependencyId = "com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT:" + testDependencyChecksum;
    }

    @Test
    public void testAddPiece() throws Exception {
        if(pieceManager.hasPiece(testId)) {
            pieceManager.disconnectPiece(pieceManager.getPiece(testId));
            pieceManager.removePiece(pieceManager.getPiece(testId));
        }

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT", "jar");

        Assert.assertTrue(piece.getDependencies().contains(testDependencyId));
        Assert.assertNotNull(piece.getClassLoader());
        Assert.assertNotNull(piece.getFile());
        Assert.assertNotNull(pieceManager.getPiece(piece.getId()));
        Assert.assertNotNull(pieceManager.getPiece(testDependencyId));
    }

    @Test
    public void testConnectPiece() throws Exception {
        if(pieceManager.hasPiece(testId)) {
            pieceManager.disconnectPiece(pieceManager.getPiece(testId));
            pieceManager.removePiece(pieceManager.getPiece(testId));
        }

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT", "jar");
        JigsawPiece dependency = pieceManager.getPiece(testDependencyId);

        Set<JigsawPiece> pieces = pieceManager.connectPiece(piece);

        Assert.assertEquals(2, pieces.size());
        Assert.assertTrue(pieces.contains(piece));
        Assert.assertTrue(pieces.contains(dependency));
        Assert.assertEquals(JigsawPieceStatus.CONNECTED, piece.getStatus());
        Assert.assertEquals(JigsawPieceStatus.CONNECTED, dependency.getStatus());
    }

    @Test
    public void testDisconnectPiece() throws Exception {
        JigsawPiece piece = pieceManager.getPiece(testId);
        JigsawPiece dependency = pieceManager.getPiece(testDependencyId);

        Set<JigsawPiece> pieces = pieceManager.disconnectPiece(piece);

        Assert.assertEquals(2, pieces.size());
        Assert.assertTrue(pieces.contains(piece));
        Assert.assertTrue(pieces.contains(dependency));
        Assert.assertEquals(JigsawPieceStatus.DISCONNECTED, piece.getStatus());
        Assert.assertEquals(JigsawPieceStatus.DISCONNECTED, dependency.getStatus());

    }

    @Test
    public void testRemove() throws Exception {
        JigsawPiece piece = pieceManager.getPiece(testId);

        pieceManager.disconnectPiece(piece);

        pieceManager.removePiece(piece);

        Assert.assertTrue(pieceManager.getPieces().isEmpty());
    }

    @Test
    public void testGetPersistedPieces() throws Exception {
        List<SimpleJigsawPiece> pieces = pieceManager.getPersistedPieces();

        Assert.assertEquals(1, pieces.size());
        Assert.assertEquals("jigsaw-test", pieces.get(0).getArtifactId());
    }
}
