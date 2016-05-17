package com.jigsaw.spring.web;

import com.jigsaw.core.JigsawConnector;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.spring.ApplicationContextManagerFactory;

/**
 * @author rhosseini
 * @date 5/17/2016
 */
public class SpringWebConnector extends JigsawConnector {

    private MergeableXmlWebApplicationContextLoader applicationContextLoader;

    @Override
    public void connect(JigsawPiece piece) {
        super.connect(piece);

        ApplicationContextManagerFactory.getInstance()
                .getApplicationContextLoaders().add(applicationContextLoader);
    }

    @Override
    public void disconnect(JigsawPiece piece) {
        super.disconnect(piece);

        ApplicationContextManagerFactory.getInstance()
                .getApplicationContextLoaders().remove(applicationContextLoader);
    }
}
