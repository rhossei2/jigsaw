package com.core.util;

public class ResourceLoaderFactory
{
  private static ResourceLoader resourceLoader;
  
  public ResourceLoaderFactory() {}
  
  public static ResourceLoader getInstance()
  {
    if (resourceLoader == null) {
      resourceLoader = new DelegatingResourceLoader();
    }
    
    return resourceLoader;
  }
}
