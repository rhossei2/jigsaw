package com.jigsaw.core;

import com.jigsaw.core.manager.JigsawPieceManager;
import com.jigsaw.core.util.JarUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Test;

import java.io.File;

/**
 * @author rhosseini
 * @date 4/21/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/jigsaw-test-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JigsawTest {

    @Autowired
    private Jigsaw jigsaw;

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
    public void testInit() throws Exception {
        Assert.assertTrue(pieceManager.hasPiece(testId));
        Assert.assertTrue(pieceManager.hasPiece(testDependencyId));
    }

    @Test
    public void testAssemble() throws Exception {

    }

    @Test
    public void testDisassemble() throws Exception {
        jigsaw.dissssemble(testId, true);

        Assert.assertFalse(pieceManager.hasPiece(testId));
        Assert.assertFalse(pieceManager.hasPiece(testDependencyId));
    }

    @Test
    public void testReassemble() throws Exception {
        jigsaw.reassemble(testId, "com.jigsaw", "jigsaw-test", "1.0.1-SNAPSHOT");

        Assert.assertFalse(pieceManager.hasPiece(testId));
        Assert.assertFalse(pieceManager.hasPiece(testDependencyId));

        String testChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test/1.0.1-SNAPSHOT/jigsaw-test-1.0.1-SNAPSHOT.jar").getFile()));
        String testId = "com.jigsaw:jigsaw-test:1.0.1-SNAPSHOT:" + testChecksum;

        String testDependencyChecksum = JarUtils.getChecksum(new File(this.getClass().getResource("/com/jigsaw/jigsaw-test-dependency/1.0.1-SNAPSHOT/jigsaw-test-dependency-1.0.1-SNAPSHOT.jar").getFile()));
        String testDependencyId = "com.jigsaw:jigsaw-test-dependency:1.0.1-SNAPSHOT:" + testDependencyChecksum;

        Assert.assertTrue(pieceManager.hasPiece(testId));
        Assert.assertTrue(pieceManager.hasPiece(testDependencyId));
    }
}
