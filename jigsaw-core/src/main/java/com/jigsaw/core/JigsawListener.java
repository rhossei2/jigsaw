package com.jigsaw.core;

import com.jigsaw.core.model.JigsawPiece;

/**
 * Listeners provide a way for pieces to listen for changes in
 * the system as pieces are assembled or disassembled
 * Created by Ramtin Hosseini on 4/9/2016.
 */
public class JigsawListener {

    private Jigsaw jigsaw;

    /**
     * A piece was assembled
     * @param piece the assembled piece
     */
    public void assembled(JigsawPiece piece) {

    }

    /**
     * A piece was disassembled
     * @param piece disassembled piece
     */
    public void disassembled(JigsawPiece piece) {

    }

    /**
     * Gets a reference to the Jigsaw system
     * @return the Jigsaw system
     */
    public Jigsaw getJigsaw() {
        return jigsaw;
    }

    /**
     * Initializes this listener.
     * @param jigsaw the Jigsaw system initializing this listener
     */
    public void init(Jigsaw jigsaw) {
        this.jigsaw = jigsaw;
    }

    /**
     * Destroys this listener
     */
    public void destroy() {

    }
}
