package com.jigsaw.core.manager;

import com.jigsaw.core.exeption.JigsawAssemblyException;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.util.JarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ClassLoaderManager {

    private static final Logger log = LoggerFactory.getLogger(ClassLoaderManager.class);

    private Map<String, Set<java.lang.ClassLoader>> moduleClasses = new HashMap<String, Set<java.lang.ClassLoader>>();

    public JigsawClassLoader addClassLoader(JigsawPiece jigsawPiece, ClassLoader parentClassLoader) {
        JarFile jar = null;
        try {
            JigsawClassLoader classLoader =
                    new JigsawClassLoader(jigsawPiece, parentClassLoader);

            jar = new JarFile(classLoader.getJigsawPiece().getFile(), true);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();

                String resourceName = jarEntry.getName();

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
        JarFile jar = null;
        try {
            jar = new JarFile(classLoader.getJigsawPiece().getFile(), true);

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();

                String resourceName = jarEntry.getName();

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

    public ClassLoader getClassLoader(String className, JigsawPiece requestingJigsawPiece) {
        List<ClassLoader> classLoaders = getClassLoaders(className, requestingJigsawPiece);
        if (classLoaders == null || classLoaders.isEmpty()) {
            return null;
        }

        return classLoaders.get(0);
    }

    public List<ClassLoader> getClassLoaders(String resourceName, JigsawPiece requestingJigsawPiece) {
        List<ClassLoader> results = new ArrayList<>();

        Set<ClassLoader> classLoaders = moduleClasses.get(resourceName);
        if (classLoaders == null) {
            return results;
        }

        for (ClassLoader classLoader : classLoaders) {
            if (classLoader instanceof JigsawClassLoader) {
                JigsawClassLoader jigsawClassLoader = (JigsawClassLoader) classLoader;

                Set<String> dependants = jigsawClassLoader.getJigsawPiece().getDependants();

                //check whether the requesting piece has a direct dependency
                if (dependants.contains(requestingJigsawPiece.getId())) {
                    results.add(classLoader);
                }

                //check whether the requesting piece shares the same ancestry
                for (String dependantId : requestingJigsawPiece.getDependants()) {
                    if (dependants.contains(dependantId)) {
                        results.add(classLoader);
                    }
                }

            } else {
                results.add(classLoader);
            }
        }

        return results;
    }

    public void addResource(String resourceName, ClassLoader classLoader) {
        Set<ClassLoader> classLoaders = moduleClasses.get(resourceName);
        if (classLoaders == null) {
            classLoaders = new HashSet<>();
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
        protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
            try {
                return super.loadClass(className, resolve);

            } catch (ClassNotFoundException e) {
                String resourceName = JarUtils.getResourceName(className);

                ClassLoader classLoader = getClassLoader(resourceName, jigsawPiece);
                if (classLoader == null) {
                    throw new ClassNotFoundException(className + " is not available to " + jigsawPiece.getId());
                }

                Class clazz = classLoader.loadClass(className);

                if (resolve) {
                    resolveClass(clazz);
                }

                return clazz;
            }
        }

        @Override
        public URL getResource(String name) {
            URL url = super.getResource(name);
            if (url == null) {
                ClassLoader classLoader = getClassLoader(name, jigsawPiece);
                if (classLoader != null) {
                    url = classLoader.getResource(name);
                }
            }

            return url;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            List<URL> resources = new ArrayList<>();

            Enumeration<URL> urls = super.getResources(name);
            while (urls.hasMoreElements()) {
                resources.add(urls.nextElement());
            }

            List<ClassLoader> classLoaders = getClassLoaders(name, jigsawPiece);
            for (ClassLoader classLoader : classLoaders) {
                resources.add(classLoader.getResource(name));
            }

            return Collections.enumeration(resources);
        }

        public Package[] getPackages() {
            return super.getPackages();
        }

        public JigsawPiece getJigsawPiece() {
            return jigsawPiece;
        }
    }
}
