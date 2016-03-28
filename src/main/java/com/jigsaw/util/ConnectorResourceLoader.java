package com.jigsaw.util;

import com.jigsaw.exeption.JigsawAssemblyException;
import com.jigsaw.model.JigsawConnector;
import com.jigsaw.model.JigsawPiece;
import java.util.Properties;

public class ConnectorResourceLoader extends AbstractResourceLoader
{
  public ConnectorResourceLoader() {}
  
  protected void loadResourceInternal(JigsawPiece piece)
  {
    if (piece.getConnector() != null) {
      return;
    }
    
    if (piece.getProperties() == null) {
      return;
    }
    
    String className = piece.getProperties().getProperty("jigsaw.connector");
    if (className == null) {
      return;
    }
    try
    {
      Class connectorClass = piece.getClassLoader().loadClass(className);
      
      JigsawConnector connector = (JigsawConnector)connectorClass.newInstance();
      
      piece.setConnector(connector);
    }
    catch (ClassNotFoundException e) {
      throw new JigsawAssemblyException("Unable to load connector", e);
    }
    catch (InstantiationException e) {
      throw new JigsawAssemblyException("Unable to load connector", e);
    }
    catch (IllegalAccessException e) {
      throw new JigsawAssemblyException("Unable to load connector", e);
    }
  }
}
