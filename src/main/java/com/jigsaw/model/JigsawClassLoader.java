package com.jigsaw.model;

import com.jigsaw.manager.ClassLoaderManagerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class JigsawClassLoader extends URLClassLoader {

    private JigsawPiece jigsawPiece;

    public JigsawClassLoader(JigsawPiece jigsawPiece, ClassLoader parentClassLoader)
            throws MalformedURLException {
        super(new URL[]{jigsawPiece.getFile().toURI().toURL()}, parentClassLoader);

        this.jigsawPiece = jigsawPiece;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException e) {
            JigsawClassLoader classLoader =
                    ClassLoaderManagerFactory.getInstance().getClassLoader(className, jigsawPiece.getId());
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
