package com.jigsaw.web.tomcat;

/**
 * Created by rhosseini on 7/25/2017.
 */
public class TomcatFactory {

    private static JigsawTomcat tomcat;

    public static JigsawTomcat getInstance() {
        return tomcat;
    }

    public static void setInstance(JigsawTomcat tomcat) {
        TomcatFactory.tomcat = tomcat;
    }
}
