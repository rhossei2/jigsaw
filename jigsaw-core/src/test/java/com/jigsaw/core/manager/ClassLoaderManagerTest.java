package com.jigsaw.core.manager;

import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.util.JarUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * Created by RH on 3/31/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/jigsaw-test-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClassLoaderManagerTest {

    private String testChecksum;

    private String testId;

    private String testDependencyChecksum;

    private String testDependencyId;

    @Autowired
    private JigsawPieceManager pieceManager;

    @Before
    public void before() throws Exception {
        testChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test/1.0.0-SNAPSHOT/jigsaw-test-1.0.0-SNAPSHOT.jar").getFile()));
        testId = "com.jigsaw:jigsaw-test:1.0.0-SNAPSHOT:" + testChecksum;

        testDependencyChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test-dependency/1.0.0-SNAPSHOT/jigsaw-test-dependency-1.0.0-SNAPSHOT.jar").getFile()));
        testDependencyId = "com.jigsaw:jigsaw-test-dependency:1.0.0-SNAPSHOT:" + testDependencyChecksum;
    }


    @Test
    public void testAddClassLoader() throws Exception {
        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        ClassLoaderManager.JigsawClassLoader classLoader = (ClassLoaderManager.JigsawClassLoader) pieceManager.getClassLoaderManager()
                .getClassLoader("com.jigsawtestdependency.Printer", piece.getId());

        Assert.assertNotNull(classLoader);
        Assert.assertEquals(testDependencyId, classLoader.getJigsawPiece().getId());

        classLoader = (ClassLoaderManager.JigsawClassLoader) pieceManager.getClassLoaderManager()
                .getClassLoader("com.jigsawtestdependency.Printer", "invalidId");

        Assert.assertNull(classLoader);
    }

    @Test
    public void testRemoveClassLoader() throws Exception {
        JigsawPiece piece = pieceManager.addPiece("com.jigsaw", "jigsaw-test", "1.0.0-SNAPSHOT");

        pieceManager.connectPiece(piece);

        pieceManager.disconnectPiece(piece);

        pieceManager.removePiece(piece);

        ClassLoaderManager.JigsawClassLoader classLoader = (ClassLoaderManager.JigsawClassLoader) pieceManager.getClassLoaderManager()
                .getClassLoader("com.jigsawtestdependency.Printer", piece.getId());

        Assert.assertNull(classLoader);
    }
}
