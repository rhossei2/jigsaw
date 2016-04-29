package com.jigsaw.spring.web;

import com.jigsaw.core.JigsawListener;
import com.jigsaw.core.model.JigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ramtin Hosseini
 * @date 4/20/2016
 */
public class SpringContextListener extends JigsawListener {

    private static final String SPRING_LOCATION_PROP = "jigsaw.spring.location";

    private static final Logger log = LoggerFactory.getLogger(SpringContextListener.class);

    @Override
    public void assembled(JigsawPiece piece) {

    }

    @Override
    public void disassembled(JigsawPiece piece) {

    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
