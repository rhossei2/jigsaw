package com.jigsaw.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;

/**
 * @author rhosseini
 * @date 5/3/2016
 */
public abstract class AbstractApplicationContextLoader {

    protected static final String SPRING_LOCATION_PROP = "jigsaw.spring.location";

    public abstract MergeableApplicationContext loadApplicationContext(JigsawPiece piece);

    public boolean canSupport(Jigsaw jigsaw, JigsawPiece piece) {
        return getSpringFileLocations(piece) != null;
    }

    protected String[] getSpringFileLocations(JigsawPiece piece) {
        if (piece.getProperties().containsKey(SPRING_LOCATION_PROP)) {
            String locationProperty = piece.getProperties().getProperty(SPRING_LOCATION_PROP);
            String[] springLocations = locationProperty.split(",");

            return springLocations;
        }

        return null;
    }
}
