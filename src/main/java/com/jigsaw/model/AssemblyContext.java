package com.jigsaw.model;

import java.util.HashSet;
import java.util.Set;


public class AssemblyContext
{
  public AssemblyContext() {}
  
  private Set<JigsawPiece> pieces = new HashSet();
  
  public Set<JigsawPiece> getPieces() {
    return pieces;
  }
  
  public void setPieces(Set<JigsawPiece> pieces) {
    this.pieces = pieces;
  }
}
