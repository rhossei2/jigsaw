package com.jigsaw.web.spring;

import com.jigsaw.core.JigsawConnector;
import com.jigsaw.core.model.JigsawPiece;

/**
 * @author rhosseini
 * @date 5/3/2016
 */
public class SpringConnector extends JigsawConnector {

    private MergeableClassPathXmlApplicationContextLoader classPathXmlApplicationContextManager;

    @Override
    public void connect(JigsawPiece piece) {
        super.connect(piece);

        classPathXmlApplicationContextManager = new MergeableClassPathXmlApplicationContextLoader();

        ApplicationContextManagerFactory.getInstance()
                .getApplicationContextLoaders().add(classPathXmlApplicationContextManager);
    }

    @Override
    public void disconnect(JigsawPiece piece) {
        ApplicationContextManagerFactory.getInstance()
                .getApplicationContextLoaders().remove(classPathXmlApplicationContextManager);

        super.disconnect(piece);
    }
}
