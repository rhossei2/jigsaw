package com.jigsaw.web.tomcat;

import com.jigsaw.web.spring.MergeableClassPathXmlApplicationContext;
import com.jigsaw.web.spring.MergeableXmlWebApplicationContext;
import com.jigsaw.web.tomcat.JigsawTomcat;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
@Ignore
public class SpringTomcatTest {

    @Test
    public void test() throws Exception {
        MergeableClassPathXmlApplicationContext parentContextOne =
                new MergeableClassPathXmlApplicationContext();
        parentContextOne.setConfigLocation("/parent-context-one.xml");
        parentContextOne.refresh();

        MergeableClassPathXmlApplicationContext parentContextTwo =
                new MergeableClassPathXmlApplicationContext();
        parentContextTwo.setConfigLocation("/parent-context-two.xml");
        parentContextTwo.refresh();

        MergeableXmlWebApplicationContext mainContext = new MergeableXmlWebApplicationContext();
        mainContext.merge(parentContextOne);
        mainContext.merge(parentContextTwo);
        mainContext.setConfigLocations("classpath:main-context.xml");
        //mainContext.refresh();

        JigsawTomcat tomcat = new JigsawTomcat();
        tomcat.setPort(2080);

        File base = new File(System.getProperty("java.io.tmpdir"));
        StandardContext rootCtx = (StandardContext) tomcat.addContext("/", base.getAbsolutePath());

        tomcat.start();

        String webappPath = getClass().getResource("/webapp").getFile();

        Context testCtx1 = tomcat.getWebappContext("/context", new File(webappPath).getAbsolutePath());
        testCtx1.addServletContainerInitializer(new SpringServletContainerInitializerMock(mainContext), null);

        tomcat.addContext(tomcat.getHost(), testCtx1);

        //testCtx.stop();
        tomcat.getServer().await();
    }
}
