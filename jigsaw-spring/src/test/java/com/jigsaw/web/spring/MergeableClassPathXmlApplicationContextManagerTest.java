package com.jigsaw.web.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.manager.JigsawPieceManager;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class MergeableClassPathXmlApplicationContextManagerTest {

    private MergeableClassPathXmlApplicationContextLoader loader =
            new MergeableClassPathXmlApplicationContextLoader();

    @Before
    public void before() throws Exception {
        ApplicationContextManagerFactory.getInstance()
                .getApplicationContextLoaders().add(loader);
    }

    @After
    public void after() throws Exception {
        ApplicationContextManagerFactory.getInstance()
                .getApplicationContextLoaders().remove(loader);
    }

    @Test
    public void testAddApplicationContext() throws Exception {
        JigsawPieceManager manager = new JigsawPieceManager();
        Jigsaw jigsaw = new Jigsaw();
        jigsaw.setPieceManager(manager);

        JigsawPiece parentOne = new JigsawPiece();
        parentOne.setProperties(new Properties());
        parentOne.getProperties().put(MergeableClassPathXmlApplicationContextLoader.SPRING_LOCATION_PROP, "/parent-context-one.xml");
        parentOne.setId("parent-one");
        parentOne.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece parentTwo = new JigsawPiece();
        parentTwo.setProperties(new Properties());
        parentTwo.getProperties().put(MergeableClassPathXmlApplicationContextLoader.SPRING_LOCATION_PROP, "/parent-context-two.xml");
        parentTwo.setId("parent-two");
        parentTwo.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece main = new JigsawPiece();
        main.setProperties(new Properties());
        main.getProperties().put(MergeableClassPathXmlApplicationContextLoader.SPRING_LOCATION_PROP, "/main-context.xml");
        main.setId("main");
        main.setStatus(JigsawPieceStatus.CONNECTED);

        main.getDependencies().add(parentOne.getId());
        main.getDependencies().add(parentTwo.getId());

        manager.getPiecesMap().put(main.getId(), main);
        manager.getPiecesMap().put(parentOne.getId(), parentOne);
        manager.getPiecesMap().put(parentTwo.getId(), parentTwo);

        ApplicationContextManager springManager = ApplicationContextManagerFactory.getInstance();

        MergeableApplicationContext context = springManager.addApplicationContext(jigsaw, main);

        Assert.assertNotNull(context);
        Assert.assertEquals(3, springManager.getContexts().size());
    }

    @Test
    public void testRemoveApplicationContext() throws Exception {
        JigsawPieceManager manager = new JigsawPieceManager();
        Jigsaw jigsaw = new Jigsaw();
        jigsaw.setPieceManager(manager);

        JigsawPiece parentOne = new JigsawPiece();
        parentOne.setProperties(new Properties());
        parentOne.getProperties().put(MergeableClassPathXmlApplicationContextLoader.SPRING_LOCATION_PROP, "/parent-context-one.xml");
        parentOne.setId("parent-one");
        parentOne.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece parentTwo = new JigsawPiece();
        parentTwo.setProperties(new Properties());
        parentTwo.getProperties().put(MergeableClassPathXmlApplicationContextLoader.SPRING_LOCATION_PROP, "/parent-context-two.xml");
        parentTwo.setId("parent-two");
        parentTwo.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece main = new JigsawPiece();
        main.setProperties(new Properties());
        main.getProperties().put(MergeableClassPathXmlApplicationContextLoader.SPRING_LOCATION_PROP, "/main-context.xml");
        main.setId("main");
        main.setStatus(JigsawPieceStatus.CONNECTED);

        main.getDependencies().add(parentOne.getId());
        main.getDependencies().add(parentTwo.getId());

        manager.getPiecesMap().put(main.getId(), main);
        manager.getPiecesMap().put(parentOne.getId(), parentOne);
        manager.getPiecesMap().put(parentTwo.getId(), parentTwo);

        ApplicationContextManager springManager = ApplicationContextManagerFactory.getInstance();

        MergeableApplicationContext context = springManager.addApplicationContext(jigsaw, main);

        Assert.assertNotNull(context);
        Assert.assertEquals(3, springManager.getContexts().size());

        main.setStatus(JigsawPieceStatus.DISCONNECTED);
        parentOne.setStatus(JigsawPieceStatus.DISCONNECTED);

        springManager.removeApplicationContext(jigsaw, main);

        Assert.assertEquals(1, springManager.getContexts().size());
        Assert.assertTrue(springManager.getContexts().containsKey("parent-two"));
    }
}
