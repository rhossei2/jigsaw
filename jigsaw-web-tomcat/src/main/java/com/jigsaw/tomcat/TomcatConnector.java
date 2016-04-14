package com.jigsaw.tomcat;

import com.jigsaw.core.JigsawConnector;
import com.jigsaw.core.exeption.JigsawConnectException;
import com.jigsaw.core.exeption.JigsawDisconnectException;
import com.jigsaw.core.model.JigsawPiece;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author rhosseini
 * @date 4/13/2016
 */
public class TomcatConnector extends JigsawConnector {

    private static final Logger log = LoggerFactory.getLogger(TomcatConnector.class);

    private Tomcat tomcat;

    @Override
    public void connect(JigsawPiece piece) {
        log.info("Starting Tomcat...");

        tomcat = new Tomcat();
        tomcat.setPort(2080);

        File base = new File(System.getProperty("java.io.tmpdir"));
        tomcat.addContext("/", base.getAbsolutePath());

        try {
            tomcat.start();

        } catch (LifecycleException e) {
            throw new JigsawConnectException("Unable to start Tomcat", e);
        }
    }

    @Override
    public void disconnect(JigsawPiece piece) {
        log.info("Stopping Tomcat...");

        if(tomcat != null) {
            try {
                tomcat.stop();

                tomcat.destroy();

            } catch (LifecycleException e) {
                throw new JigsawDisconnectException("Unable to stop and destroy Tomcat", e);
            }
        }
    }
}
