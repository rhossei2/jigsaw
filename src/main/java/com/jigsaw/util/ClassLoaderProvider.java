package com.jigsaw.util;

import com.jigsaw.exeption.JigsawAssemblyException;
import com.jigsaw.model.JigsawClassLoader;
import com.jigsaw.model.JigsawJar;
import com.jigsaw.model.JigsawPiece;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;







public class ClassLoaderProvider
{
  private Map<String, Set<JigsawClassLoader>> moduleClasses = new HashMap();
  
  public ClassLoaderProvider() {}
  
  public void addClassLoader(JigsawClassLoader classLoader) {
    try { JigsawJar jar = new JigsawJar(new File(classLoader.getJigsawPiece().getUrl().toURI()), true);
      for (String packageName : jar.getPackageNames()) {
        addResource(packageName, classLoader);
      }
      
      for (String resourceName : jar.getResourceNames()) {
        addResource(resourceName, classLoader);
      }
      
      jar.close();
    }
    catch (IOException e) {
      throw new JigsawAssemblyException("Unable to scan resources in " + classLoader.getJigsawPiece().getId(), e);
    }
    catch (URISyntaxException e) {
      throw new JigsawAssemblyException("Unable to scan resources in " + classLoader.getJigsawPiece().getId(), e);
    }
    
    Package[] packages = classLoader.getPackages();
    for (Package pack : packages) {
      Set<JigsawClassLoader> classLoaders = (Set)moduleClasses.get(pack.getName());
      if (classLoaders == null) {
        classLoaders = new HashSet();
      }
      
      classLoaders.add(classLoader);
      
      moduleClasses.put(pack.getName(), classLoaders);
    }
  }
  
  public JigsawClassLoader getClassLoader(String className, JigsawPiece requestingJigsawPiece) {
    String packageName = JarUtils.getPackageName(className);
    
    Set<JigsawClassLoader> classLoaders = (Set)moduleClasses.get(packageName);
    if (classLoaders == null) {
      return null;
    }
    
    for (JigsawClassLoader classLoader : classLoaders) {
      Set<String> dependants = classLoader.getJigsawPiece().getDependants();
      if (dependants.contains(requestingJigsawPiece.getId())) {
        return classLoader;
      }
    }
    
    return null;
  }
  
  public void removeClassLoader(JigsawPiece piece) {
    for (Package p : piece.getClassLoader().getPackages()) {
      Set<JigsawClassLoader> classLoaders = (Set)moduleClasses.get(p.getName());
      if (classLoaders != null) {
        classLoaders.remove(piece);
      }
    }
  }
  
  protected void addResource(String resourceName, JigsawClassLoader classLoader) {
    Set<JigsawClassLoader> classLoaders = (Set)moduleClasses.get(resourceName);
    if (classLoaders == null) {
      classLoaders = new HashSet();
    }
    
    classLoaders.add(classLoader);
    
    moduleClasses.put(resourceName, classLoaders);
  }
}
