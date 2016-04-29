package com.jigsaw.spring.web;

import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.spring.AbstractApplicationContextManager;
import com.jigsaw.spring.MergeableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class MergeableXmlWebApplicationContextManager
    extends AbstractApplicationContextManager {

    public static final String SPRING_WEB_ENABLED = "jigsaw.spring.web.enabled";

    private static final Logger log = LoggerFactory.getLogger(MergeableXmlWebApplicationContextManager.class);

    private ServletContext servletContext;

    public MergeableXmlWebApplicationContextManager(AbstractApplicationContextManager parent,
                                                    ServletContext servletContext) {
        super(parent);
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
    protected void refresh(MergeableApplicationContext context) {
        context.refresh();

        //set the context as an attribute so Spring servletDispatcher can reference it
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }
}
