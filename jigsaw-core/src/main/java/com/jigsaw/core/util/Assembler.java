package com.jigsaw.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jigsaw.commons.exeption.JigsawAssemblyException;
import com.jigsaw.commons.model.JigsawPiece;
import com.jigsaw.commons.model.SimpleJigsawPiece;
import com.jigsaw.core.manager.JigsawPieceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RH on 4/5/2016.
 */
public class Assembler {

    private static final Logger log = LoggerFactory.getLogger(Assembler.class);

    private String dbPath;

    private JigsawPieceManager pieceManager;

    public void assemble() {
        Reader reader = null;
        try {
            reader = new InputStreamReader(
                    this.getClass().getResourceAsStream(dbPath), "UTF-8");

            Type listType = new TypeToken<ArrayList<SimpleJigsawPiece>>() {}.getType();

            Gson gson = new GsonBuilder().create();
            List<SimpleJigsawPiece> dbPieces = gson.fromJson(reader, listType);
            for(SimpleJigsawPiece dbPiece : dbPieces) {
                JigsawPiece piece = pieceManager
                        .addPiece(dbPiece.getGroupId(), dbPiece.getArtifactId(), dbPiece.getVersion());

                if(dbPiece.getStatus() == SimpleJigsawPiece.Status.CONNECTED) {
                    pieceManager.connectPiece(piece);
                }
            }

        } catch (IOException e) {
            throw new JigsawAssemblyException("Unable to assemble jigsaw pieces", e);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new JigsawAssemblyException("Unable to close db file", e);
                }
            }
        }
    }

    public void disAssemble() {
        for(JigsawPiece piece : pieceManager.getPieces()) {
            if(piece.getDependants().isEmpty()) {
                pieceManager.removePiece(piece);
            }
        }
    }

    public JigsawPiece assemble(String groupId, String artifactId, String version) {
        JigsawPiece piece = pieceManager.addPiece(groupId, artifactId, version);

        pieceManager.connectPiece(piece);

        return piece;
    }

    public JigsawPiece reAssemble(String pieceId, String groupId, String artifactId, String version) {
        JigsawPiece oldPiece = pieceManager.getPiece(pieceId);
        if (oldPiece != null) {
            pieceManager.removePiece(oldPiece);
        }

        JigsawPiece newPiece = pieceManager.addPiece(groupId, artifactId, version);

        pieceManager.connectPiece(newPiece);

        return newPiece;
    }

    public void disAssemble(String pieceId) {
        JigsawPiece piece = pieceManager.getPiece(pieceId);
        if(piece == null) {
            return;
        }

        pieceManager.removePiece(piece);
    }

    public void setPieceManager(JigsawPieceManager pieceManager) {
        this.pieceManager = pieceManager;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }
}
