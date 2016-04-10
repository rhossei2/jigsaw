package com.jigsaw.core.model;

import com.jigsaw.core.util.JarUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class JigsawJar extends JarFile {

    private List<String> packageNames = new ArrayList();

    private List<String> resourceNames = new ArrayList();

    public JigsawJar(String name, boolean loadResources) throws IOException {
        super(name);

        if (loadResources) {
            loadResources();
        }
    }

    public JigsawJar(File file, boolean loadResources) throws IOException {
        super(file);

        if (loadResources) {
            loadResources();
        }
    }

    protected void loadResources() {
        Enumeration<JarEntry> entries = entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();

            String resourceName = jarEntry.getName();
            if (JarUtils.isClass(jarEntry)) {
                packageNames.add(JarUtils.getPackageName(resourceName));
            } else if (JarUtils.isResource(jarEntry)) {
                resourceNames.add("/" + resourceName);
            }
        }
    }

    public List<String> getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(List<String> packageNames) {
        this.packageNames = packageNames;
    }

    public List<String> getResourceNames() {
        return resourceNames;
    }

    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
    }
}
