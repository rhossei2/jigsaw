package com.jigsaw.web.spring;

import com.jigsaw.core.JigsawListener;
import com.jigsaw.core.model.JigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ramtin Hosseini
 * @date 4/20/2016
 */
public class SpringContextListener extends JigsawListener {

    private static final Logger log = LoggerFactory.getLogger(SpringContextListener.class);

    @Override
    public void assembled(JigsawPiece piece) {
        ApplicationContextManager springManager =
                ApplicationContextManagerFactory.getInstance();

        springManager.addApplicationContext(getJigsaw(), piece);
    }

    @Override
    public void disassembled(JigsawPiece piece) {
        ApplicationContextManager springManager =
                ApplicationContextManagerFactory.getInstance();

        springManager.removeApplicationContext(getJigsaw(), piece);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
