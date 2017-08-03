package com.jigsaw.web.tomcat;

import com.jigsaw.core.JigsawConnector;
import com.jigsaw.core.exeption.JigsawConnectException;
import com.jigsaw.core.exeption.JigsawDisconnectException;
import com.jigsaw.core.model.JigsawPiece;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by rhosseini on 7/25/2017.
 */
public class JigsawTomcatConnector extends JigsawConnector {

    private static final Logger log = LoggerFactory.getLogger(JigsawTomcatConnector.class);

    @Override
    public void connect(JigsawPiece piece) throws JigsawConnectException {
        if (TomcatFactory.getInstance() != null) {
            log.warn("Tomcat is already initialized");

            return;
        }

        JigsawTomcat tomcat = new JigsawTomcat();
        tomcat.setPort(2080);

        File base = new File(System.getProperty("java.io.tmpdir"));
        StandardContext rootCtx = (StandardContext) tomcat.addContext("/", base.getAbsolutePath());

        try {
            tomcat.start();

            TomcatFactory.setInstance(tomcat);

        } catch (LifecycleException e) {
            throw new JigsawConnectException("Unable to start Tomcat", e);
        }
    }

    @Override
    public void disconnect(JigsawPiece piece) throws JigsawDisconnectException {
        if (TomcatFactory.getInstance() != null) {
            try {
                TomcatFactory.getInstance().stop();

                TomcatFactory.setInstance(null);

            } catch (LifecycleException e) {
                throw new JigsawDisconnectException("Unable to stop Tomcat", e);
            }
        }
    }
}
