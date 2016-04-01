package com.jigsaw.manager;

import com.jigsaw.exeption.JigsawAssemblyException;
import com.jigsaw.model.JigsawClassLoader;
import com.jigsaw.model.JigsawJar;
import com.jigsaw.model.JigsawPiece;
import com.jigsaw.util.JarUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ClassLoaderManager {

    private Map<String, Set<JigsawClassLoader>> moduleClasses = new HashMap();

    public JigsawClassLoader addClassLoader(JigsawPiece jigsawPiece, ClassLoader parentClassLoader) {
        JigsawJar jar = null;
        try {
            JigsawClassLoader classLoader = new JigsawClassLoader(jigsawPiece, parentClassLoader);

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
            if(jar != null){
                try {
                    jar.close();

                } catch (IOException e) {
                    throw new JigsawAssemblyException("Unable to close jar", e);
                }
            }
        }
    }

    public void removeClassLoader(JigsawClassLoader classLoader) {
        JigsawJar jar= null;
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
            if(jar != null){
                try {
                    jar.close();

                } catch (IOException e) {
                    throw new JigsawAssemblyException("Unable to close jar", e);
                }
            }
        }
    }

    public JigsawClassLoader getClassLoader(String className, String requestingJigsawPieceId) {
        String packageName = JarUtils.getPackageName(className);

        Set<JigsawClassLoader> classLoaders = moduleClasses.get(packageName);
        if (classLoaders == null) {
            return null;
        }

        for (JigsawClassLoader classLoader : classLoaders) {
            Set<String> dependants = classLoader.getJigsawPiece().getDependants();
            if (dependants.contains(requestingJigsawPieceId)) {
                return classLoader;
            }
        }

        return null;
    }

    protected void addResource(String resourceName, JigsawClassLoader classLoader) {
        Set<JigsawClassLoader> classLoaders = moduleClasses.get(resourceName);
        if (classLoaders == null) {
            classLoaders = new HashSet<JigsawClassLoader>();
        }

        classLoaders.add(classLoader);

        moduleClasses.put(resourceName, classLoaders);
    }

    protected void removeResource(String resourceName, JigsawClassLoader classLoader) {
        Set<JigsawClassLoader> classLoaders = moduleClasses.get(resourceName);
        if (classLoaders == null) {
            return;
        }

        classLoaders.remove(classLoader);

        if(classLoaders.isEmpty()) {
            moduleClasses.remove(resourceName);
        }
    }
}
