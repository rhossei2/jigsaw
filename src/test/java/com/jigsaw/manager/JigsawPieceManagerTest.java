package com.jigsaw.manager;

import com.jigsaw.model.JigsawPiece;
import com.jigsaw.model.SimpleJigsawPiece;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by RH on 3/28/2016.
 */
public class JigsawPieceManagerTest {

    @Test
    public void testAddPiece() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        Assert.assertTrue(piece.getDependencies().contains("com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT"));
        Assert.assertNotNull(piece.getClassLoader());
        Assert.assertNotNull(piece.getUrl());
        Assert.assertNotNull(pieceManager.getPiece(piece.getId()));
        Assert.assertNotNull(pieceManager.getPiece("com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT"));
    }

    @Test
    public void testConnectPiece() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");
        JigsawPiece dependency = pieceManager.getPiece("com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT");

        Set<JigsawPiece> pieces = pieceManager.connect(piece);

        Assert.assertEquals(2, pieces.size());
        Assert.assertTrue(pieces.contains(piece));
        Assert.assertTrue(pieces.contains(dependency));
        Assert.assertEquals(SimpleJigsawPiece.Status.CONNECTED, piece.getStatus());
        Assert.assertEquals(SimpleJigsawPiece.Status.CONNECTED, dependency.getStatus());

    }
}
