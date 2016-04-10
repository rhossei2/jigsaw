package com.jigsaw.core.util;

import com.jigsaw.core.model.JigsawPiece;

import java.util.ArrayList;
import java.util.List;


public class DelegatingResourceLoader implements ResourceLoader {

    private List<AbstractResourceLoader> loaders = new ArrayList<AbstractResourceLoader>();

    public void loadResources(JigsawPiece piece) {
        for (ResourceLoader loader : loaders) {
            loader.loadResources(piece);
        }
    }

    public void setLoaders(List<AbstractResourceLoader> loaders) {
        this.loaders = loaders;
    }
}
