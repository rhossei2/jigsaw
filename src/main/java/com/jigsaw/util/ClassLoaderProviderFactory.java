package com.jigsaw.util;

public class ClassLoaderProviderFactory
{
  private static ClassLoaderProvider classLoaderProvider;
  
  public ClassLoaderProviderFactory() {}
  
  public static ClassLoaderProvider getInstance()
  {
    if (classLoaderProvider == null) {
      classLoaderProvider = new ClassLoaderProvider();
    }
    return classLoaderProvider;
  }
}
