package com.jigsaw.core.manager;

import com.jigsaw.core.exeption.JigsawAssemblyException;
import com.jigsaw.core.model.JigsawJar;
import com.jigsaw.core.model.JigsawPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


public class ClassLoaderManager {

    private static final Logger log = LoggerFactory.getLogger(ClassLoaderManager.class);

    private Map<String, Set<java.lang.ClassLoader>> moduleClasses = new HashMap<String, Set<java.lang.ClassLoader>>();

    public JigsawClassLoader addClassLoader(JigsawPiece jigsawPiece, ClassLoader parentClassLoader) {
        JigsawJar jar = null;
        try {
            JigsawClassLoader classLoader =
                    new JigsawClassLoader(jigsawPiece, parentClassLoader);

            jar = new JigsawJar(classLoader.getJigsawPiece().getFile(), true);
            for (String className : jar.getClassNames()) {
                addResource(className, classLoader);
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

            for (String className : jar.getClassNames()) {
                removeResource(className, classLoader);
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
        public Class<?> loadClass(String className) throws ClassNotFoundException {
            try {
                return super.loadClass(className);

            } catch (ClassNotFoundException e) {
                ClassLoader classLoader = getClassLoader(className, jigsawPiece);
                if (classLoader == null) {
                    throw new ClassNotFoundException(className + " is not available to " + jigsawPiece.getId());
                }

                return classLoader.loadClass(className);
            }
        }

        @Override
        public URL getResource(String name) {
            log.info("Getting resource " + name);
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
            log.info("Getting resource " + name);

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
