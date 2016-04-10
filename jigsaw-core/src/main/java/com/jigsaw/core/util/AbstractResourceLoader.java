package com.jigsaw.core.util;

import com.jigsaw.core.exeption.JigsawAssemblyException;
import com.jigsaw.core.model.JigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractResourceLoader
  implements ResourceLoader
{
  private static final String JIGSAW_PROPERTIES = "jigsaw.properties";
  
  public AbstractResourceLoader() {}
  
  private static final Logger log = LoggerFactory.getLogger(AbstractResourceLoader.class);
  
  public void loadResources(JigsawPiece piece) {
    loadProperties(piece);
    
    loadResourceInternal(piece);
  }
  
  protected abstract void loadResourceInternal(JigsawPiece paramJigsawPiece);
  
  protected void loadProperties(JigsawPiece piece) {
    if (piece.getProperties() != null) {
      return;
    }
    
    InputStream inputStream = piece.getClassLoader().getResourceAsStream("jigsaw.properties");
    try {
      Properties properties = new Properties();
      if (inputStream == null) {
        log.info("No jigsaw.properties found in " + piece.getId());

      }
      else
      {
        properties.load(inputStream);
        
        piece.setProperties(properties);
      }
      return;
    } catch (IOException e) { throw new JigsawAssemblyException("Unable to load properties from JigsawPiece", e);
    }
    finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        }
        catch (IOException e) {
          throw new JigsawAssemblyException("Unable to load properties from JigsawPiece", e);
        }
      }
    }
  }
}
