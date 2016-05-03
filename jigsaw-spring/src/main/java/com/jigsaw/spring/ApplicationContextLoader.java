package com.jigsaw.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;

/**
 * @author rhosseini
 * @date 5/3/2016
 */
public abstract class ApplicationContextLoader {

    protected abstract MergeableApplicationContext loadApplicationContext(JigsawPiece piece);

    public abstract boolean canSupport(Jigsaw jigsaw, JigsawPiece piece);
}
