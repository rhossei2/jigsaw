package com.jigsaw.core.util;

import com.jigsaw.commons.model.JigsawPiece;
import com.jigsaw.core.manager.ClassLoaderManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class JigsawClassLoader extends URLClassLoader {

    private JigsawPiece jigsawPiece;

    private ClassLoaderManager classLoaderManager;

    public JigsawClassLoader(JigsawPiece jigsawPiece,
                             ClassLoader parentClassLoader,
                             ClassLoaderManager classLoaderManager)
            throws MalformedURLException {
        super(new URL[]{jigsawPiece.getFile().toURI().toURL()}, parentClassLoader);

        this.jigsawPiece = jigsawPiece;
        this.classLoaderManager = classLoaderManager;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException e) {
            ClassLoader classLoader = classLoaderManager
                    .getClassLoader(className, jigsawPiece.getId());
            if (classLoader == null) {
                throw new ClassNotFoundException("Unable to find a ClassLoader for class: " + className);
            }

            return classLoader.loadClass(className);
        }
    }

    public Package[] getPackages() {
        return super.getPackages();
    }

    public JigsawPiece getJigsawPiece() {
        return jigsawPiece;
    }
}
