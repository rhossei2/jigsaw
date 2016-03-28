package com.jigsaw.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JigsawListener
{
  public JigsawListener() {}
  
  private static Logger log = LoggerFactory.getLogger(JigsawListener.class);
  
  public void connected(JigsawPiece jigsawPiece) {
    log.info("Connected piece " + jigsawPiece.getId());
  }
  
  public void disconnected(JigsawPiece jigsawPiece) {
    log.info("Disconnected piece " + jigsawPiece.getId());
  }
  
  public void assemblyCompletion(AssemblyContext assemblyContext) {}
  
  public void assemblyInitiation(AssemblyContext assemblyContext) {}
}
