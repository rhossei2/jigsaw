package com.jigsaw.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jigsaw.core.exeption.JigsawAssemblyException;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.SimpleJigsawPiece;
import com.jigsaw.core.manager.JigsawPieceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RH on 4/9/2016.
 */
public class Jigsaw {

    private String dbPath;

    private JigsawPieceManager pieceManager;

    private static final Logger log = LoggerFactory.getLogger(Jigsaw.class);

    public void assemble() {
        Reader reader = null;
        try {
            InputStream is = this.getClass().getResourceAsStream(dbPath);
            if(is == null) {
                log.warn("No db file found at " + dbPath);

                return;
            }

            reader = new InputStreamReader(is, "UTF-8");

            Type listType = new TypeToken<ArrayList<SimpleJigsawPiece>>() {
            }.getType();

            Gson gson = new GsonBuilder().create();
            List<SimpleJigsawPiece> dbPieces = gson.fromJson(reader, listType);
            for (SimpleJigsawPiece dbPiece : dbPieces) {
                JigsawPiece piece = pieceManager
                        .addPiece(dbPiece.getGroupId(), dbPiece.getArtifactId(), dbPiece.getVersion());

                if (dbPiece.getStatus() == SimpleJigsawPiece.Status.CONNECTED) {
                    pieceManager.connectPiece(piece);
                    if(piece.getListener() != null) {
                        piece.getListener().setJigsaw(this);
                    }
                }
            }

        } catch (IOException e) {
            throw new JigsawAssemblyException("Unable to assemble jigsaw pieces", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new JigsawAssemblyException("Unable to close db file", e);
                }
            }
        }
    }

    public void disAssemble() {
        for (JigsawPiece piece : pieceManager.getPieces()) {
            if (piece.getDependants().isEmpty()) {
                pieceManager.removePiece(piece);
            }
        }
    }

    public JigsawPiece assemble(String groupId, String artifactId, String version) {
        JigsawPiece piece = pieceManager.addPiece(groupId, artifactId, version);

        pieceManager.connectPiece(piece);
        if(piece.getListener() != null) {
            piece.getListener().setJigsaw(this);
        }

        return piece;
    }

    public JigsawPiece reAssemble(String pieceId, String groupId, String artifactId, String version) {
        JigsawPiece oldPiece = pieceManager.getPiece(pieceId);
        if (oldPiece != null) {
            pieceManager.removePiece(oldPiece);
        }

        JigsawPiece newPiece = pieceManager.addPiece(groupId, artifactId, version);

        pieceManager.connectPiece(newPiece);
        if(newPiece.getListener() != null) {
            newPiece.getListener().setJigsaw(this);
        }

        return newPiece;
    }

    public void disAssemble(String pieceId, boolean remove) {
        JigsawPiece piece = pieceManager.getPiece(pieceId);
        if (piece == null) {
            return;
        }

        if(remove) {
            pieceManager.removePiece(piece);
        } else {
            pieceManager.disconnectPiece(piece);
        }
    }

    protected void invokeAssemblyListeners(JigsawPiece jigsawPiece) {
        for(JigsawPiece piece : pieceManager.getPieces()) {
            if(piece.getDependants().isEmpty() && piece.getListener() != null &&
                    !piece.getId().equals(jigsawPiece.getId())) {
                piece.getListener().assembled(jigsawPiece);
            }
        }
    }

    protected void invokeDisAssemblyListeners(JigsawPiece jigsawPiece) {
        for(JigsawPiece piece : pieceManager.getPieces()) {
            if(piece.getDependants().isEmpty() && piece.getListener() != null &&
                    !piece.getId().equals(jigsawPiece.getId())) {
                piece.getListener().disAssembled(jigsawPiece);
            }
        }
    }

    public void setPieceManager(JigsawPieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public JigsawPieceManager getPieceManager() {
        return pieceManager;
    }
}
