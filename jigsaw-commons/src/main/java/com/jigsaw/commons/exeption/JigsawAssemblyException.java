package com.jigsaw.commons.exeption;


public class JigsawAssemblyException
  extends RuntimeException
{
  public JigsawAssemblyException(String message)
  {
    super(message);
  }
  
  public JigsawAssemblyException(String message, Throwable e) {
    super(message, e);
  }
}
