package com.jigsaw;

import com.jigsaw.model.JigsawPiece;
import com.jigsaw.util.JigsawAssembler;
import com.jigsaw.util.JsonUtils;
import java.io.PrintStream;
import java.util.Collection;

public class Test
{
  public Test() {}
  
  public static void main(String[] args) throws Exception
  {
    JigsawAssembler assembler = new JigsawAssembler();
    assembler.setLocalRepository("C:\\Users\\RH\\.m2\\repository");
    

    JigsawPiece piece = assembler.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");
    
    assembler.connect(piece);
    
    Collection<JigsawPiece> installed = assembler.getPieces();
    
    System.out.println(JsonUtils.toString(assembler.getPieces()));
  }
}
