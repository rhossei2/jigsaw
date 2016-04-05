package com.jigsaw.commons.model;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class JigsawPiece extends SimpleJigsawPiece {

    private Properties properties;

    private JigsawListener listener;

    private JigsawConnector connector;

    private ClassLoader classLoader;

    private File file;

    private Set<String> dependants = new HashSet();

    private Set<String> dependencies = new HashSet();

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Set<String> getDependants() {
        return dependants;
    }

    public void setDependants(Set<String> dependants) {
        this.dependants = dependants;
    }

    public JigsawListener getListener() {
        return listener;
    }

    public void setListener(JigsawListener listener) {
        this.listener = listener;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    public JigsawConnector getConnector() {
        return connector;
    }

    public void setConnector(JigsawConnector connector) {
        this.connector = connector;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
