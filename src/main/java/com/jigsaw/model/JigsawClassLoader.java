package com.jigsaw.model;

import com.jigsaw.util.ClassLoaderProviderFactory;

import java.net.URL;
import java.net.URLClassLoader;





public class JigsawClassLoader
  extends URLClassLoader
{
  private JigsawPiece jigsawPiece;
  
  public JigsawClassLoader(JigsawPiece jigsawPiece, ClassLoader parentClassLoader)
  {
    super(new URL[] { jigsawPiece.getUrl() }, parentClassLoader);
    
    this.jigsawPiece = jigsawPiece;
  }

  @Override
  public Class<?> loadClass(String className) throws ClassNotFoundException
  {
    try
    {
      return super.loadClass(className);
    }
    catch (ClassNotFoundException e)
    {
      JigsawClassLoader classLoader = ClassLoaderProviderFactory.getInstance().getClassLoader(className, jigsawPiece);
      
      if (classLoader == null) {
        throw new ClassNotFoundException("Unable to find a ClassLoader for class: " + className);
      }
      
      return classLoader.loadClass(className);
    }
  }
  
  public Package[] getPackages()
  {
    return super.getPackages();
  }
  
  public JigsawPiece getJigsawPiece() {
    return jigsawPiece;
  }
}
