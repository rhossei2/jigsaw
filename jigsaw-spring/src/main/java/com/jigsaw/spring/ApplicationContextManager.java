package com.jigsaw.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class ApplicationContextManager {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextManager.class);

    protected Map<String, MergeableApplicationContext> contexts = new HashMap<>();

    private Set<ApplicationContextLoader> applicationContextLoaders = new HashSet<>();

    public MergeableApplicationContext addApplicationContext(Jigsaw jigsaw, JigsawPiece piece) {
        if (contexts.containsKey(piece.getId())) {
            return contexts.get(piece.getId());
        }

        MergeableApplicationContext context = null;
        if (piece.getStatus() == JigsawPieceStatus.CONNECTED) {
            for (ApplicationContextLoader contextLoader : applicationContextLoaders) {
                if (contextLoader.canSupport(jigsaw, piece)) {
                    context = contextLoader.loadApplicationContext(piece);
                }
            }
        }

        for(String dependencyId : piece.getDependencies()) {
            MergeableApplicationContext dependencyContext = addApplicationContext(jigsaw,
                    jigsaw.getPieceManager().getPiece(dependencyId));

            if (dependencyContext != null) {
                if (context != null) {
                    context.merge(dependencyContext);
                }

                contexts.put(dependencyId, dependencyContext);
            }
        }

        if (context != null) {
            log.info("Refreshing applicationContext for " + piece.getId());

            context.refresh();

            contexts.put(piece.getId(), context);
        }

        return context;
    }

    public void removeApplicationContext(Jigsaw jigsaw, JigsawPiece piece) {
        if (piece.getStatus() == JigsawPieceStatus.CONNECTED ||
                !contexts.containsKey(piece.getId())) {
            return;
        }

        //if this piece has an application context, destroy it
        MergeableApplicationContext context = contexts.get(piece.getId());
        if (context != null) {
            log.info("Closing applicationContext for " + piece.getId());

            context.close();

            contexts.remove(piece.getId());
        }

        //if the piece has dependencies that have been disconnected, destroy them
        for (String dependencyId : piece.getDependencies()) {
            removeApplicationContext(jigsaw, jigsaw.getPieceManager().getPiece(dependencyId));
        }
    }

    public Map<String, MergeableApplicationContext> getContexts() {
        return contexts;
    }

    public Set<ApplicationContextLoader> getApplicationContextLoaders() {
        return applicationContextLoaders;
    }
}
