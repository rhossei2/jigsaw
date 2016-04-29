package com.jigsaw.core;

import com.jigsaw.core.model.JigsawPiece;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class Test {

    public static void main(String[] args) throws Exception {
        JigsawBuilder builder = new JigsawBuilder();

        Jigsaw jigsaw = builder.create();
        jigsaw.getPieceManager().setLocalRepository("C:/Users/rhosseini/.m2/repository");

        JigsawPiece piece = jigsaw.assemble("com.jigsaw", "jigsaw-spring-web", "1.0.0-SNAPSHOT");
    }
}
