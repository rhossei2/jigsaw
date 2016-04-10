package com.jigsaw.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by RH on 4/9/2016.
 */
public class JigsawFactory {

    private ClassPathXmlApplicationContext applicationContext;

    public Jigsaw create() {
        if(applicationContext != null) {
            applicationContext = new ClassPathXmlApplicationContext("/jigsaw-context.xml");
        }

        return (Jigsaw) applicationContext.getBean("jigsaw");
    }

    public void destroy() {
        if(applicationContext != null) {
            applicationContext.close();
        }
    }
}
