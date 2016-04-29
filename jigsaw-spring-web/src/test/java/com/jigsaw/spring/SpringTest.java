package com.jigsaw.spring;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
@Ignore
public class SpringTest {

    @Test
    public void testSpring() throws Exception {
        MergeableClassPathXmlApplicationContext parentContextOne =
                new MergeableClassPathXmlApplicationContext();
        parentContextOne.setConfigLocation("/parent-context-one.xml");
        parentContextOne.refresh();

        MergeableClassPathXmlApplicationContext parentContextTwo =
                new MergeableClassPathXmlApplicationContext();
        parentContextTwo.setConfigLocation("/parent-context-two.xml");
        parentContextTwo.refresh();

        MergeableClassPathXmlApplicationContext mainContext = new MergeableClassPathXmlApplicationContext();
        mainContext.merge(parentContextOne);
        mainContext.merge(parentContextTwo);
        mainContext.setConfigLocations("/main-context.xml");
        mainContext.refresh();

        mainContext.destroy();

    }
}
