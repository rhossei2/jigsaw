package com.jigsaw.core.util;

import org.apache.commons.codec.digest.DigestUtils;

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
        String[] array = className.replace(".class", "").replace("/", ".").split("\\.");

        String packageName = "";
        for (int i = 0; i < array.length - 1; i++) {
            if (i > 0) {
                packageName = packageName + ".";
            }
            packageName = packageName + array[i];
        }

        return packageName;
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
}
