package com.jigsaw.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;






public class JigsawPiece
  extends SimpleJigsawPiece
{
  private Properties properties;
  private JigsawListener listener;
  private JigsawConnector connector;
  private JigsawClassLoader classLoader;
  private URL url;
  
  public JigsawPiece() {}
  
  private Set<String> dependants = new HashSet();
  
  private Set<String> dependencies = new HashSet();
  
  public URL getUrl() {
    return url;
  }
  
  public void setUrl(URL url) {
    this.url = url;
  }
  
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
}
