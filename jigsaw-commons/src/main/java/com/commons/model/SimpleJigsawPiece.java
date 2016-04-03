package com.commons.model;


public class SimpleJigsawPiece {

    private String id;

    private String groupId;

    private String artifactId;

    private String version;

    private Status status;

    public SimpleJigsawPiece() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        SimpleJigsawPiece that = (SimpleJigsawPiece) o;

        return id.equals(id);
    }


    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return "SimpleJigsawPiece{id='" + id + '\'' + ", groupId='" + groupId + '\'' + ", artifactId='" + artifactId + '\'' + ", version='" + version + '\'' + ", status='" + status + '\'' + '}';
    }


    public static enum Status {
        CONNECTED,

        DISCONNECTED;

        private Status() {
        }
    }
}
