package com.jigsaw.spring.web;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.spring.ApplicationContextLoader;
import com.jigsaw.spring.MergeableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class MergeableXmlWebApplicationContextManager
    extends ApplicationContextLoader {

    public static final String SPRING_WEB_ENABLED = "jigsaw.spring.web.enabled";

    private static final Logger log = LoggerFactory.getLogger(MergeableXmlWebApplicationContextManager.class);

    private ServletContext servletContext;

    public MergeableXmlWebApplicationContextManager(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected MergeableApplicationContext loadApplicationContext(JigsawPiece piece) {
        MergeableXmlWebApplicationContext context = null;
        if (piece.getProperties().containsKey(SPRING_WEB_ENABLED)) {
            boolean springEnabled = Boolean.valueOf(piece.getProperties().getProperty(SPRING_WEB_ENABLED));
            if (!springEnabled) {
                return null;
            }

            log.info("Loading web application context for " + piece.getId());

            context = new MergeableXmlWebApplicationContext();
            context.setClassLoader(piece.getClassLoader());
            context.setServletContext(servletContext);

            String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation");
            if (contextConfigLocation != null && !contextConfigLocation.isEmpty()) {
                String[] locations = contextConfigLocation.split(",");

                context.setConfigLocations(locations);
            }

        }

        return context;
    }

    @Override
    public boolean canSupport(Jigsaw jigsaw, JigsawPiece piece) {
        return piece.getProperties().containsKey(SPRING_WEB_ENABLED);
    }
}
