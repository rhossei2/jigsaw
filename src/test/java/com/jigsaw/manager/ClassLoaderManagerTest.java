package com.jigsaw.manager;

import com.jigsaw.model.JigsawClassLoader;
import com.jigsaw.model.JigsawPiece;
import com.jigsaw.util.JarUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by RH on 3/31/2016.
 */
public class ClassLoaderManagerTest {

    private String testChecksum;

    private String testId;

    private String testDependencyChecksum;

    private String testDependencyId;

    @Before
    public void before() throws Exception {
        testChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test/1.0.0-SNAPSHOT/jigsaw-test-1.0.0-SNAPSHOT.jar").getFile()));
        testId = "com.jigsaw:jigsaw-test:1.0.0-SNAPSHOT:" + testChecksum;

        testDependencyChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test-dependency/1.0.0-SNAPSHOT/jigsaw-test-dependency-1.0.0-SNAPSHOT.jar").getFile()));
        testDependencyId = "com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT:" + testDependencyChecksum;
    }

    @After
    public void after() throws Exception {
        ClassLoaderManagerFactory.setInstance(null);
    }


    @Test
    public void testAddClassLoader() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        JigsawClassLoader classLoader = pieceManager.getClassLoaderManager()
                .getClassLoader("com.jigsawtestdependency.Printer", piece.getId());

        Assert.assertNotNull(classLoader);
        Assert.assertEquals(testDependencyId, classLoader.getJigsawPiece().getId());

        classLoader = pieceManager.getClassLoaderManager()
                .getClassLoader("com.jigsawtestdependency.Printer", "invalidId");

        Assert.assertNull(classLoader);
    }

    @Test
    public void testRemoveClassLoader() throws Exception {
        JigsawPieceManager pieceManager = new JigsawPieceManager();
        pieceManager.setLocalRepository(this.getClass().getResource("/").getFile());

        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        pieceManager.connectPiece(piece);

        pieceManager.removePiece(piece);

        JigsawClassLoader classLoader = pieceManager.getClassLoaderManager()
                .getClassLoader("com.jigsawtestdependency.Printer", piece.getId());

        Assert.assertNull(classLoader);
    }
}
