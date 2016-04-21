package com.jigsaw.spring;

import com.jigsaw.core.JigsawListener;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ramtin Hosseini
 * @date 4/20/2016
 */
public class SpringContextListener extends JigsawListener {

    private static final String SPRING_LOCATION_PROP = "jigsaw.spring.location";

    private static final Logger log = LoggerFactory.getLogger(SpringContextListener.class);

    private Map<String, MergeableApplicationContext> contexts =
            new ConcurrentHashMap<>();

    @Override
    public void assembled(JigsawPiece piece) {
        if(!piece.getProperties().containsKey(SPRING_LOCATION_PROP)) {
            return;
        }

        String[] springLocations = piece.getProperties().getProperty(SPRING_LOCATION_PROP).split(",");

        log.info("Processing Spring files at " + springLocations + " for piece " + piece.getId());

        MergeableApplicationContext context = new MergeableApplicationContext();
        context.setConfigLocations(springLocations);
        context.setClassLoader(piece.getClassLoader());

        //make all the dependency application contexts available to this piece
        for(String dependencyId : piece.getDependencies()) {
            if(contexts.containsKey(dependencyId)) {
                context.merge(contexts.get(dependencyId));
            }
        }

        context.refresh();

        contexts.put(piece.getId(), context);
    }

    @Override
    public void disassembled(JigsawPiece piece) {
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
            disassembled(getJigsaw().getPieceManager().getPiece(dependencyId));
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
