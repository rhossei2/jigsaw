package com.jigsaw.core;

import com.jigsaw.core.manager.JigsawPieceManager;
import com.jigsaw.core.model.JigsawPiece;

import java.util.Collection;

public class Test {

    public static void main(String[] args) throws Exception {
        JigsawPieceManager assembler = new JigsawPieceManager();
        assembler.setLocalRepository("C:\\Users\\RH\\.m2\\repository");

        JigsawPiece piece = assembler.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        assembler.connectPiece(piece);

        Collection<JigsawPiece> installed = assembler.getPieces();
    }
}
