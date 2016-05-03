package com.jigsaw.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class MergeableClassPathXmlApplicationContextLoader
        extends ApplicationContextLoader {

    public static final String SPRING_LOCATION_PROP = "jigsaw.spring.location";

    private static final Logger log = LoggerFactory.getLogger(MergeableClassPathXmlApplicationContextLoader.class);

    @Override
    protected MergeableApplicationContext loadApplicationContext(JigsawPiece piece) {
        MergeableClassPathXmlApplicationContext context = null;
        if (piece.getProperties().containsKey(SPRING_LOCATION_PROP)) {
            String[] springLocations = piece.getProperties().getProperty(SPRING_LOCATION_PROP).split(",");

            log.info("Loading Spring files at " + springLocations + " for piece " + piece.getId());

            context = new MergeableClassPathXmlApplicationContext();
            context.setConfigLocations(springLocations);
            context.setClassLoader(piece.getClassLoader());
        }

        return context;
    }

    @Override
    public boolean canSupport(Jigsaw jigsaw, JigsawPiece piece) {
        return piece.getProperties().containsKey(SPRING_LOCATION_PROP);
    }
}
