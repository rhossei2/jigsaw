package com.jigsaw;

import com.jigsaw.manager.JigsawPieceManager;
import com.jigsaw.model.JigsawPiece;
import com.jigsaw.util.JarUtils;
import com.jigsaw.util.JsonUtils;

import java.io.File;
import java.util.Collection;

public class Test {
  public Test() {}
  
  public static void main(String[] args) throws Exception {
    JigsawPieceManager assembler = new JigsawPieceManager();
    assembler.setLocalRepository("C:\\Users\\RH\\.m2\\repository");

    JigsawPiece piece = assembler.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");
    
    assembler.connectPiece(piece);
    
    Collection<JigsawPiece> installed = assembler.getPieces();
    
    System.out.println(JsonUtils.toString(assembler.getPieces()));
    System.out.println(JarUtils.getChecksum(new File("C:\\Users\\RH\\.m2\\repository\\com\\jigsaw\\jigsaw\\1.0.0-SNAPSHOT\\jigsaw-1.0.0-SNAPSHOT.jar")));
  }
}
