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

        JigsawPiece piece = jigsaw.assemble("com.jigsaw", "jigsaw-spring", "1.0.0-SNAPSHOT");

        jigsaw.assemble("com.healthways", "moai-jar", "1.0.0-SNAPSHOT");
        //jigsaw.assemble("com.healthways.module", "com-healthways-service-dao", "1.3.4-SNAPSHOT");
    }
}
