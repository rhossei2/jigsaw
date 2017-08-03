package com.jigsaw.web.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.model.JigsawPiece;
import org.springframework.web.context.WebApplicationContext;

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

    private MergeableXmlWebApplicationContextLoader applicationContextLoader;

    public SpringServletContainerInitializer(Jigsaw jigsaw, JigsawPiece piece) {
        this.jigsaw = jigsaw;
        this.piece = piece;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
/*        MergeableXmlWebApplicationContextManager manager = new MergeableXmlWebApplicationContextManager(
                        ApplicationContextManagerFactory.getInstance(),
                        servletContext);

        manager.addApplicationContext(jigsaw, piece);

        ApplicationContextManagerFactory.getInstance().getContexts().get(piece.getId());

        context.setServletContext(servletContext);
        context.setClassLoader(this.getClass().getClassLoader());
        context.refresh();*/

        applicationContextLoader = new MergeableXmlWebApplicationContextLoader(servletContext);

        ApplicationContextManagerFactory.getInstance()
                .addApplicationContext(jigsaw, piece, applicationContextLoader);
    }
}
