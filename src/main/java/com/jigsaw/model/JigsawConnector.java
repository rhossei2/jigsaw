package com.jigsaw.model;

public abstract interface JigsawConnector
{
  public abstract void connect(JigsawPiece paramJigsawPiece);
  
  public abstract void disconnect(JigsawPiece paramJigsawPiece);
}
