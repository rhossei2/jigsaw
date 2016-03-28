package com.jigsaw.util;

import java.util.jar.JarEntry;

public class JarUtils
{
  public JarUtils() {}
  
  public static String getPackageName(String className)
  {
    String[] array = className.replace(".class", "").replace("/", ".").split("\\.");
    
    String packageName = "";
    for (int i = 0; i < array.length - 1; i++) {
      if (i > 0) {
        packageName = packageName + ".";
      }
      packageName = packageName + array[i];
    }
    
    return packageName;
  }
  
  public static boolean isClass(JarEntry jarEntry) {
    if (jarEntry.getName().endsWith(".class")) {
      return true;
    }
    
    return false;
  }
  
  public static boolean isResource(JarEntry jarEntry) {
    if (!jarEntry.isDirectory()) {
      return true;
    }
    
    return false;
  }
}
