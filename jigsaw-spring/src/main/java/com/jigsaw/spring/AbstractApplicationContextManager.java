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
 * @date 4/29/2016
 */
public abstract class AbstractApplicationContextManager {

    private static final Logger log = LoggerFactory.getLogger(AbstractApplicationContextManager.class);

    private Map<String, MergeableApplicationContext> contexts = new HashMap<>();

    private AbstractApplicationContextManager parentApplicationContextManager;

    public AbstractApplicationContextManager() {}

    public AbstractApplicationContextManager(AbstractApplicationContextManager parentApplicationContextManager) {
        this.parentApplicationContextManager = parentApplicationContextManager;
    }

    public MergeableApplicationContext addApplicationContext(Jigsaw jigsaw, JigsawPiece piece) {
        if (parentApplicationContextManager != null) {
            if (parentApplicationContextManager.getContexts().containsKey(piece.getId())) {
                return parentApplicationContextManager.getContexts().get(piece.getId());
            }
        }

        if (contexts.containsKey(piece.getId())) {
            return contexts.get(piece.getId());
        }

        MergeableApplicationContext context = null;
        if (piece.getStatus() == JigsawPieceStatus.CONNECTED) {
            context = loadApplicationContext(piece);
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

            refresh(context);

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

            close(context);

            contexts.remove(piece.getId());
        }

        //if the piece has dependencies that have been disconnected, destroy them
        for (String dependencyId : piece.getDependencies()) {
            removeApplicationContext(jigsaw, jigsaw.getPieceManager().getPiece(dependencyId));
        }
    }

    protected abstract MergeableApplicationContext loadApplicationContext(JigsawPiece piece);

    protected void refresh(MergeableApplicationContext context) {
        context.refresh();
    }

    protected void close(MergeableApplicationContext context) {
        context.close();
    }

    public Map<String, MergeableApplicationContext> getContexts() {
        return contexts;
    }
}
