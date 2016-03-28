package com.jigsaw.util;

import com.jigsaw.model.JigsawPiece;
import java.util.Arrays;
import java.util.List;




public class DelegatingResourceLoader
  implements ResourceLoader
{
  private List<AbstractResourceLoader> loaders = Arrays.asList(new AbstractResourceLoader[] { new ConnectorResourceLoader(), new ListenerResourceLoader() });
  
  public DelegatingResourceLoader() {}
  
  public void loadResources(JigsawPiece piece) { for (ResourceLoader loader : loaders) {
      loader.loadResources(piece);
    }
  }
}
