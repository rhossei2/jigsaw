package com.jigsaw.core.util;

import com.jigsaw.core.exeption.JigsawAssemblyException;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.aether.artifact.Artifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;

public class JarUtils {

    public static String getChecksum(File jar) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jar);

            return DigestUtils.sha1Hex(fis);

        } finally {
            fis.close();
        }
    }

    public static String getPackageName(String className) {
        String[] array = getClassName(className).split("\\.");

        String packageName = "";
        for (int i = 0; i < array.length - 1; i++) {
            if (i > 0) {
                packageName = packageName + ".";
            }
            packageName = packageName + array[i];
        }

        return packageName;
    }

    public static String getClassName(String resourceName) {
        return resourceName.replace(".class", "").replace("/", ".");
    }

    public static String getResourceName(String className) {
        return className.replace(".", "/") + ".class";
    }

    public static boolean isClass(JarEntry jarEntry) {
        if (jarEntry.getName().endsWith(".class")) {
            return true;
        }

        return false;
    }

    public static boolean isResource(JarEntry jarEntry) {
        if (!jarEntry.isDirectory()) {
            return true;
        }

        return false;
    }

    public static String generateId(Artifact artifact) {
        try {
            StringBuffer sb = new StringBuffer()
                    .append(artifact.getGroupId())
                    .append(":")
                    .append(artifact.getArtifactId())
                    .append(":")
                    .append(artifact.getVersion())
                    .append(":")
                    .append(getChecksum(artifact.getFile()));

            return sb.toString();

        } catch (IOException e) {
            throw new JigsawAssemblyException("Unable to generate an id for the artifact", e);
        }
    }
}
