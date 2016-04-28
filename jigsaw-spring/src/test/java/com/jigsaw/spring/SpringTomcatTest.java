package com.jigsaw.spring;

import org.apache.catalina.core.StandardContext;
import org.junit.Test;

import java.io.File;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class SpringTomcatTest {

    @Test
    public void test() throws Exception {
        MergeableApplicationContext parentContextOne =
                new MergeableApplicationContext();
        parentContextOne.setConfigLocation("/parent-context-one.xml");
        parentContextOne.refresh();

        MergeableApplicationContext parentContextTwo =
                new MergeableApplicationContext();
        parentContextTwo.setConfigLocation("/parent-context-two.xml");
        parentContextTwo.refresh();

        MergeableWebApplicationContext mainContext = new MergeableWebApplicationContext();
        mainContext.merge(parentContextOne);
        mainContext.merge(parentContextTwo);
        mainContext.setConfigLocations("classpath:main-context.xml");
        //mainContext.refresh();

        JigsawTomcat tomcat = new JigsawTomcat();
        tomcat.setPort(2080);

        File base = new File(System.getProperty("java.io.tmpdir"));
        StandardContext rootCtx = (StandardContext) tomcat.addContext("/", base.getAbsolutePath());

        tomcat.start();

        StandardContext testCtx = (StandardContext) tomcat.getWebappContext("/context", getClass().getResource("/webapp").getFile());
        testCtx.addServletContainerInitializer(new SpringServletContainerInitializer(mainContext), null);

        tomcat.addContext(tomcat.getHost(), testCtx);

        tomcat.getServer().await();

        //testCtx.stop();
    }
}
