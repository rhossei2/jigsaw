package com.core.util;

import com.commons.exeption.JigsawAssemblyException;
import com.commons.model.JigsawConnector;
import com.commons.model.JigsawPiece;

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
