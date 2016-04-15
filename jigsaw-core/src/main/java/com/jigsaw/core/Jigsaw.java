package com.jigsaw.core;

import com.jigsaw.core.manager.JigsawPieceManager;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import com.jigsaw.core.model.SimpleJigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The Jigsaw platform
 * Created by Ramtin Hosseini on 4/9/2016.
 */
public class Jigsaw {

    private JigsawPieceManager pieceManager;

    private static final Logger log = LoggerFactory.getLogger(Jigsaw.class);

    /**
     * Assembles all the pieces from scratch by connecting them together
     */
    public void assemble() {
        List<SimpleJigsawPiece> dbPieces = pieceManager.getPersistedPieces();
        for (SimpleJigsawPiece dbPiece : dbPieces) {
            JigsawPiece piece = pieceManager
                    .addPiece(dbPiece.getGroupId(), dbPiece.getArtifactId(), dbPiece.getVersion());

            if (dbPiece.getStatus() == JigsawPieceStatus.CONNECTED) {
                pieceManager.connectPiece(piece);

                invokeAssemblyListeners(piece);
            }
        }
    }

    /**
     * Disassembles all the pieces from the platform by disconnecting
     * and removing them from the platform
     */
    public void dissssemble() {
        for (JigsawPiece piece : pieceManager.getPieces()) {
            if(piece.getDependants().isEmpty()) {
                pieceManager.removePiece(piece);
            }
        }
    }

    /**
     * Assembles a new piece and connects it to the platform
     * @param groupId piece group id
     * @param artifactId piece artifact id
     * @param version piece version
     * @return the newly assembled piece
     */
    public JigsawPiece assemble(String groupId, String artifactId, String version) {
        JigsawPiece piece = pieceManager.addPiece(groupId, artifactId, version);

        pieceManager.connectPiece(piece);

        invokeAssemblyListeners(piece);

        pieceManager.persistPieces();

        return piece;
    }

    /**
     * Replaces an existing piece with a new piece.
     * @param pieceId the piece to reassemble
     * @param groupId the new group id
     * @param artifactId the new artifact id
     * @param version the new version
     * @return the newly assembled piece
     */
    public JigsawPiece reassemble(String pieceId, String groupId, String artifactId, String version) {
        JigsawPiece oldPiece = pieceManager.getPiece(pieceId);
        if (oldPiece != null) {
            pieceManager.removePiece(oldPiece);
        }

        JigsawPiece newPiece = pieceManager.addPiece(groupId, artifactId, version);

        pieceManager.connectPiece(newPiece);

        invokeAssemblyListeners(newPiece);

        pieceManager.persistPieces();

        return newPiece;
    }

    /**
     * Disassemble a piece from the platform and by disconnecting and
     * removing it from the plateform
     * @param pieceId piece to disassemble
     * @param remove true if the piece should be destroyed completely.
     *               False, to only disconnect the piece from the platform
     */
    public void dissssemble(String pieceId, boolean remove) {
        JigsawPiece piece = pieceManager.getPiece(pieceId);
        if (piece == null) {
            return;
        }

        if(remove) {
            pieceManager.removePiece(piece);

        } else {
            pieceManager.disconnectPiece(piece);
        }

        pieceManager.persistPieces();
    }

    protected void invokeAssemblyListeners(JigsawPiece jigsawPiece) {
        //start with the dependencies
        for(String dependencyId : jigsawPiece.getDependencies()) {
            invokeAssemblyListeners(pieceManager.getPiece(dependencyId));
        }

        for(JigsawPiece piece : pieceManager.getPieces()) {
            if(piece.getListener() != null && !piece.getId().equals(jigsawPiece.getId())) {
                piece.getListener().setJigsaw(this);
                piece.getListener().assembled(jigsawPiece);
            }
        }
    }

    protected void invokeDisAssemblyListeners(JigsawPiece jigsawPiece) {
        for(JigsawPiece piece : pieceManager.getPieces()) {
            if(piece.getListener() != null && !piece.getId().equals(jigsawPiece.getId())) {
                piece.getListener().disAssembled(jigsawPiece);
            }
        }
    }

    public void setPieceManager(JigsawPieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    public JigsawPieceManager getPieceManager() {
        return pieceManager;
    }
}
