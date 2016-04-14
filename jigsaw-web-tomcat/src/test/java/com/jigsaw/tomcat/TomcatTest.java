package com.jigsaw.tomcat;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.junit.Test;

import java.io.File;

/**
 * @author rhosseini
 * @date 4/13/2016
 */
public class TomcatTest {

    @Test
    public void testTomcat() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(2080);

        File base = new File(System.getProperty("java.io.tmpdir"));
        StandardContext rootCtx = (StandardContext) tomcat.addContext("/", base.getAbsolutePath());

        tomcat.start();

        //StandardContext testCtx = (StandardContext) tomcat.addContext("/context", getClass().getResource("/").getFile());
        tomcat.addWebapp(tomcat.getHost(), "/context", getClass().getResource("/webapp").getFile());
/*        Tomcat.addServlet(testCtx, "testServlet", new TestServlet());
        testCtx.addServletMapping("/test", "testServlet");*/

        tomcat.getServer().await();


    }
}
