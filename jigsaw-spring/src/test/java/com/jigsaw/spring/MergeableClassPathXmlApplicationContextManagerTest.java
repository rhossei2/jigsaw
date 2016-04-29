package com.jigsaw.spring;

import com.jigsaw.core.Jigsaw;
import com.jigsaw.core.manager.JigsawPieceManager;
import com.jigsaw.core.model.JigsawPiece;
import com.jigsaw.core.model.JigsawPieceStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public class MergeableClassPathXmlApplicationContextManagerTest {

    @Test
    public void testAddApplicationContext() throws Exception {
        JigsawPieceManager manager = new JigsawPieceManager();
        Jigsaw jigsaw = new Jigsaw();
        jigsaw.setPieceManager(manager);

        JigsawPiece parentOne = new JigsawPiece();
        parentOne.getProperties().put(MergeableClassPathXmlApplicationContextManager.SPRING_LOCATION_PROP, "/parent-context-one.xml");
        parentOne.setId("parent-one");
        parentOne.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece parentTwo = new JigsawPiece();
        parentTwo.getProperties().put(MergeableClassPathXmlApplicationContextManager.SPRING_LOCATION_PROP, "/parent-context-two.xml");
        parentTwo.setId("parent-two");
        parentTwo.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece main = new JigsawPiece();
        main.getProperties().put(MergeableClassPathXmlApplicationContextManager.SPRING_LOCATION_PROP, "/main-context.xml");
        main.setId("main");
        main.setStatus(JigsawPieceStatus.CONNECTED);

        main.getDependencies().add(parentOne.getId());
        main.getDependencies().add(parentTwo.getId());

        manager.getPiecesMap().put(main.getId(), main);
        manager.getPiecesMap().put(parentOne.getId(), parentOne);
        manager.getPiecesMap().put(parentTwo.getId(), parentTwo);

        MergeableClassPathXmlApplicationContextManager springManager =
                new MergeableClassPathXmlApplicationContextManager();

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
        parentOne.getProperties().put(MergeableClassPathXmlApplicationContextManager.SPRING_LOCATION_PROP, "/parent-context-one.xml");
        parentOne.setId("parent-one");
        parentOne.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece parentTwo = new JigsawPiece();
        parentTwo.getProperties().put(MergeableClassPathXmlApplicationContextManager.SPRING_LOCATION_PROP, "/parent-context-two.xml");
        parentTwo.setId("parent-two");
        parentTwo.setStatus(JigsawPieceStatus.CONNECTED);

        JigsawPiece main = new JigsawPiece();
        main.getProperties().put(MergeableClassPathXmlApplicationContextManager.SPRING_LOCATION_PROP, "/main-context.xml");
        main.setId("main");
        main.setStatus(JigsawPieceStatus.CONNECTED);

        main.getDependencies().add(parentOne.getId());
        main.getDependencies().add(parentTwo.getId());

        manager.getPiecesMap().put(main.getId(), main);
        manager.getPiecesMap().put(parentOne.getId(), parentOne);
        manager.getPiecesMap().put(parentTwo.getId(), parentTwo);

        MergeableClassPathXmlApplicationContextManager springManager =
                new MergeableClassPathXmlApplicationContextManager();

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
