package com.jigsaw.web.spring;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class ApplicationContextManagerFactory {

    private static ApplicationContextManager manager;

    public static ApplicationContextManager getInstance() {
        if (manager == null) {
            manager = new ApplicationContextManager();
        }

        return manager;
    }
}
