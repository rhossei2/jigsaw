package com.jigsaw.spring.web;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class SpringServletContainerInitializer implements ServletContainerInitializer {

    private JigsawPiece piece;

    private Jigsaw jigsaw;

    public SpringServletContainerInitializer(Jigsaw jigsaw, JigsawPiece piece) {
        this.jigsaw = jigsaw;
        this.piece = piece;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
/*        MergeableXmlWebApplicationContextManager manager = new MergeableXmlWebApplicationContextManager(
                        ApplicationContextManagerFactory.getInstance(),
                        servletContext);

        manager.addApplicationContext(jigsaw, piece);*/
    }
}
