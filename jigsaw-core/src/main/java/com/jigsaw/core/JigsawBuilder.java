package com.jigsaw.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by RH on 4/9/2016.
 */
public class JigsawBuilder {

    private ClassPathXmlApplicationContext applicationContext;

    public Jigsaw create() {
        if(applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext("/jigsaw-context.xml");
            applicationContext.setClassLoader(this.getClass().getClassLoader());
        }

        return (Jigsaw) applicationContext.getBean("jigsaw");
    }

    public void destroy() {
        if(applicationContext != null) {
            applicationContext.close();
        }
    }
}
