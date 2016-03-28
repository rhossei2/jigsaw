package com.jigsaw.util;

import com.jigsaw.exeption.JigsawAssemblyException;
import com.jigsaw.model.JigsawListener;
import com.jigsaw.model.JigsawPiece;
import java.util.Properties;

public class ListenerResourceLoader extends AbstractResourceLoader
{
  public ListenerResourceLoader() {}
  
  protected void loadResourceInternal(JigsawPiece piece)
  {
    if (piece.getListener() != null) {
      return;
    }
    
    if (piece.getProperties() == null) {
      return;
    }
    
    String className = piece.getProperties().getProperty("jigsaw.listener");
    if (className == null) {
      return;
    }
    try
    {
      Class listenerClass = piece.getClassLoader().loadClass(className);
      
      JigsawListener listener = (JigsawListener)listenerClass.newInstance();
      
      piece.setListener(listener);
    }
    catch (ClassNotFoundException e) {
      throw new JigsawAssemblyException("Unable to load listener", e);
    }
    catch (InstantiationException e) {
      throw new JigsawAssemblyException("Unable to load listener", e);
    }
    catch (IllegalAccessException e) {
      throw new JigsawAssemblyException("Unable to load listener", e);
    }
  }
}
