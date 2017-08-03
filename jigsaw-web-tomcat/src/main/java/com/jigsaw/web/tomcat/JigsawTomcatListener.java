package com.jigsaw.web.tomcat;

import com.jigsaw.core.JigsawListener;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.web.spring.SpringServletContainerInitializer;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rhosseini on 7/25/2017.
 */
public class JigsawTomcatListener extends JigsawListener {

    private static final String CONTEXT_PROP = "jigsaw.web.tomcat.context";

    private Map<String, Context> contexts = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(JigsawTomcatListener.class);

    @Override
    public void assembled(JigsawPiece piece) {
        String context = piece.getProperties().getProperty(CONTEXT_PROP);
        if (StringUtils.isEmpty(context)) {
            context = "/";
        }

        URL webappUrl = piece.getClassLoader().getResource("webapp");
        if (webappUrl == null) {
            log.warn("No webapp folder found");

            return;
        }

        String webappPath = webappUrl.getPath().replace("file:/", "");

        JigsawTomcat tomcat = TomcatFactory.getInstance();

        WebappLoader webappLoader = new WebappLoader(piece.getClassLoader());

        Context webappContext = tomcat.getWebappContext(context, webappPath);
        webappContext.addServletContainerInitializer(new SpringServletContainerInitializer(getJigsaw(), piece), null);
        webappContext.setLoader(webappLoader);
        //webappContext.setParentClassLoader(piece.getClassLoader());

        tomcat.addContext(tomcat.getHost(), webappContext);

        contexts.put(piece.getId(), webappContext);
    }

    @Override
    public void disassembled(JigsawPiece piece) {
        if (contexts.containsKey(piece.getId())) {
            try {
                contexts.get(piece.getId()).stop();

            } catch (LifecycleException e) {
                log.error("Unable to stop tomcat context for piece " + piece.getId());
            }
        }

        super.disassembled(piece);
    }
}
