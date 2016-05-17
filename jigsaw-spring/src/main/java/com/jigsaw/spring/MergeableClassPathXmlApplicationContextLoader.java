package com.jigsaw.spring;

import com.jigsaw.core.model.JigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class MergeableClassPathXmlApplicationContextLoader
        extends AbstractApplicationContextLoader {

    private static final Logger log = LoggerFactory.getLogger(MergeableClassPathXmlApplicationContextLoader.class);

    @Override
    public MergeableApplicationContext loadApplicationContext(JigsawPiece piece) {
        String[] springLocations = getSpringFileLocations(piece);
        if (springLocations == null || springLocations.length == 0) {
            return null;
        }

        log.info("Found Spring files at " + Arrays.toString(springLocations));

        MergeableClassPathXmlApplicationContext context = new MergeableClassPathXmlApplicationContext();
        context.setConfigLocations(springLocations);
        context.setClassLoader(piece.getClassLoader());

        return context;
    }
}
