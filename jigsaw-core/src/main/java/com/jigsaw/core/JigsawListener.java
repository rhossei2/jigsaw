package com.jigsaw.core;

import com.jigsaw.core.model.JigsawPiece;

/**
 * Created by RH on 4/9/2016.
 */
public class JigsawListener {

    private Jigsaw jigsaw;

    public void assembled(JigsawPiece piece) {

    }

    public void disAssembled(JigsawPiece piece) {

    }

    public Jigsaw getJigsaw() {
        return jigsaw;
    }

    public void setJigsaw(Jigsaw jigsaw) {
        this.jigsaw = jigsaw;
    }
}
