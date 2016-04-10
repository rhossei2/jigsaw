package com.jigsaw.core.manager;

import com.jigsaw.core.exeption.JigsawAssemblyException;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.util.JarUtils;
import com.jigsaw.core.model.JigsawJar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ClassLoaderManager {

    private Map<String, Set<java.lang.ClassLoader>> moduleClasses = new HashMap<String, Set<java.lang.ClassLoader>>();

    public JigsawClassLoader addClassLoader(JigsawPiece jigsawPiece, java.lang.ClassLoader parentClassLoader) {
        JigsawJar jar = null;
        try {
            JigsawClassLoader classLoader =
                    new JigsawClassLoader(jigsawPiece, parentClassLoader);

            jar = new JigsawJar(classLoader.getJigsawPiece().getFile(), true);
            for (String packageName : jar.getPackageNames()) {
                addResource(packageName, classLoader);
            }

            for (String resourceName : jar.getResourceNames()) {
                addResource(resourceName, classLoader);
            }

            return classLoader;

        } catch (IOException e) {
            throw new JigsawAssemblyException("Unable to scan resources in " + jigsawPiece.getId(), e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();

                } catch (IOException e) {
                    throw new JigsawAssemblyException("Unable to close jar", e);
                }
            }
        }
    }

    public void removeClassLoader(JigsawClassLoader classLoader) {
        JigsawJar jar = null;
        try {
            jar = new JigsawJar(classLoader.getJigsawPiece().getFile(), true);

            for (String packageName : jar.getPackageNames()) {
                removeResource(packageName, classLoader);
            }

            for (String resourceName : jar.getResourceNames()) {
                removeResource(resourceName, classLoader);
            }

            classLoader.close();

        } catch (IOException e) {
            throw new JigsawAssemblyException("Unable to scan resources in " + classLoader.getJigsawPiece().getId(), e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();

                } catch (IOException e) {
                    throw new JigsawAssemblyException("Unable to close jar", e);
                }
            }
        }
    }

    public ClassLoader getClassLoader(String className, String requestingJigsawPieceId) {
        String packageName = JarUtils.getPackageName(className);

        Set<ClassLoader> classLoaders = moduleClasses.get(packageName);
        if (classLoaders == null) {
            return null;
        }

        for (ClassLoader classLoader : classLoaders) {
            if (classLoader instanceof JigsawClassLoader) {
                Set<String> dependants = ((JigsawClassLoader) classLoader).getJigsawPiece().getDependants();
                if (dependants.contains(requestingJigsawPieceId)) {
                    return classLoader;
                }

            } else {
                return classLoader;
            }
        }

        return null;
    }

    public void addResource(String resourceName, ClassLoader classLoader) {
        Set<ClassLoader> classLoaders = moduleClasses.get(resourceName);
        if (classLoaders == null) {
            classLoaders = new HashSet<ClassLoader>();
        }

        classLoaders.add(classLoader);

        moduleClasses.put(resourceName, classLoaders);
    }

    public void removeResource(String resourceName, ClassLoader classLoader) {
        Set<ClassLoader> classLoaders = moduleClasses.get(resourceName);
        if (classLoaders == null) {
            return;
        }

        classLoaders.remove(classLoader);

        if (classLoaders.isEmpty()) {
            moduleClasses.remove(resourceName);
        }
    }

    public class JigsawClassLoader extends URLClassLoader {

        private JigsawPiece jigsawPiece;

        public JigsawClassLoader(JigsawPiece jigsawPiece,
                                 ClassLoader parentClassLoader)
                throws MalformedURLException {
            super(new URL[]{jigsawPiece.getFile().toURI().toURL()}, parentClassLoader);

            this.jigsawPiece = jigsawPiece;
        }

        @Override
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            try {
                return super.loadClass(className);
            } catch (ClassNotFoundException e) {
                ClassLoader classLoader =
                        getClassLoader(className, jigsawPiece.getId());

                if (classLoader == null) {
                    throw new ClassNotFoundException("Unable to find a JigsawClassLoader for class: " + className);
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
}
