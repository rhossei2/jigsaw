package com.jigsaw.spring.web;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.spring.AbstractApplicationContextLoader;
import com.jigsaw.spring.MergeableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.Arrays;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class MergeableXmlWebApplicationContextLoader
    extends AbstractApplicationContextLoader {

    private static final Logger log = LoggerFactory.getLogger(MergeableXmlWebApplicationContextLoader.class);

    private ServletContext servletContext;

    public MergeableXmlWebApplicationContextLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public MergeableApplicationContext loadApplicationContext(JigsawPiece piece) {
        MergeableXmlWebApplicationContext context = new MergeableXmlWebApplicationContext();
        context.setClassLoader(piece.getClassLoader());
        context.setServletContext(servletContext);

        String[] springLocations = getSpringFileLocations(piece);
        if (springLocations != null) {
            log.info("Found Spring Web files at " + Arrays.toString(springLocations));

            context.setConfigLocations(springLocations);
        }

        return context;
    }

    @Override
    public boolean canSupport(Jigsaw jigsaw, JigsawPiece piece) {
        return super.canSupport(jigsaw, piece) && piece.getExtension().equals("war");
    }

    @Override
    protected String[] getSpringFileLocations(JigsawPiece piece) {
        //search jigsaw.properties for file locations
        String[] files = super.getSpringFileLocations(piece);
        if (files == null) {
            //search web.xml for the file locations
            String contextConfigLocation = servletContext.getInitParameter("contextConfigLocation");
            if (contextConfigLocation != null && !contextConfigLocation.isEmpty()) {
                files = contextConfigLocation.split(",");
            }
        }

        return files;
    }
}
