package com.jigsaw.manager;

public class ClassLoaderManagerFactory {
    private static ClassLoaderManager classLoaderManager;

    public static ClassLoaderManager getInstance() {
        if (classLoaderManager == null) {
            classLoaderManager = new ClassLoaderManager();
        }
        return classLoaderManager;
    }

    public static void setInstance(ClassLoaderManager manager) {
        classLoaderManager = manager;
    }
}
