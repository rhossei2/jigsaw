package com.jigsaw.spring;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class MergeableClassPathXmlApplicationContextManagerFactory {

    private static MergeableClassPathXmlApplicationContextManager manager;

    public static MergeableClassPathXmlApplicationContextManager getInstance() {
        if (manager == null) {
            manager = new MergeableClassPathXmlApplicationContextManager();
        }

        return manager;
    }
}
