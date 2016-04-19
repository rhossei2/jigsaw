package com.jigsaw.spring;

import org.junit.Test;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class SpringTest {

    @Test
    public void testSpring() throws Exception {
        MergeableApplicationContext parentContextOne =
                new MergeableApplicationContext();
        parentContextOne.setConfigLocation("/parent-context-one.xml");
        parentContextOne.refresh();

        MergeableApplicationContext parentContextTwo =
                new MergeableApplicationContext();
        parentContextTwo.setConfigLocation("/parent-context-two.xml");
        parentContextTwo.refresh();

        MergeableApplicationContext mainContext = new MergeableApplicationContext(parentContextOne, parentContextTwo);
        mainContext.setConfigLocations("/main-context.xml");
        mainContext.refresh();

        mainContext.destroy();

    }
}
