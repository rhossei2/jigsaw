package com.jigsaw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class JigsawPiece extends SimpleJigsawPiece {

    private Properties properties;

    private JigsawListener listener;

    private JigsawConnector connector;

    private JigsawClassLoader classLoader;

    private File file;

    private Set<String> dependants = new HashSet();

    private Set<String> dependencies = new HashSet();

    @JsonIgnore
    public JigsawClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(JigsawClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Set<String> getDependants() {
        return dependants;
    }

    public void setDependants(Set<String> dependants) {
        this.dependants = dependants;
    }

    @JsonIgnore
    public JigsawListener getListener() {
        return listener;
    }

    public void setListener(JigsawListener listener) {
        this.listener = listener;
    }

    @JsonIgnore
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

    @JsonIgnore
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
