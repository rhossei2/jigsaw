package com.jigsaw.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class ApplicationContextManager {

    private static final String SPRING_LOCATION_PROP = "jigsaw.spring.location";

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextManager.class);

    private Map<String, MergeableApplicationContext> contexts =
            new HashMap<>();

    public MergeableApplicationContext addApplicationContext(Jigsaw jigsaw, JigsawPiece piece) {
        if (contexts.containsKey(piece.getId())) {
            return contexts.get(piece.getId());
        }

        if (!piece.getProperties().containsKey(SPRING_LOCATION_PROP)) {
            return null;
        }

        String[] springLocations = piece.getProperties().getProperty(SPRING_LOCATION_PROP).split(",");

        log.info("Processing Spring files at " + springLocations + " for piece " + piece.getId());

        MergeableApplicationContext context = new MergeableApplicationContext();
        context.setConfigLocations(springLocations);
        context.setClassLoader(piece.getClassLoader());

        for(String dependencyId : piece.getDependencies()) {
            MergeableApplicationContext dependencyContext = addApplicationContext(jigsaw,
                    jigsaw.getPieceManager().getPiece(dependencyId));

            if (dependencyContext != null) {
                context.merge(dependencyContext);

                contexts.put(dependencyId, dependencyContext);
            }
        }

        context.refresh();

        contexts.put(piece.getId(), context);

        return context;
    }

    public void removeApplicationContext(Jigsaw jigsaw, JigsawPiece piece) {
        if (piece.getStatus() == JigsawPieceStatus.CONNECTED ||
                !contexts.containsKey(piece.getId())) {
            return;
        }

        //if this piece has an application context, destroy it
        MergeableApplicationContext context = contexts.get(piece.getId());

        context.destroy();

        contexts.remove(piece.getId());

        //if the piece has dependencies that have been disconnected, destroy them
        for (String dependencyId : piece.getDependencies()) {
            removeApplicationContext(jigsaw, jigsaw.getPieceManager().getPiece(dependencyId));
        }
    }


}
