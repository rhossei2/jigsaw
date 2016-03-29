package com.jigsaw.manager;

public class ClassLoaderManagerFactory
{
  private static ClassLoaderManager classLoaderManager;
  
  public ClassLoaderManagerFactory() {}
  
  public static ClassLoaderManager getInstance()
  {
    if (classLoaderManager == null) {
      classLoaderManager = new ClassLoaderManager();
    }
    return classLoaderManager;
  }
}
