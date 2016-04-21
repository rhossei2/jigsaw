package com.jigsaw.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class MergeableApplicationContext extends ClassPathXmlApplicationContext {

    private List<MergeableApplicationContext> mergedContext = new ArrayList<>();

    public MergeableApplicationContext() {
    }

    public void merge(MergeableApplicationContext applicationContext) {
        mergedContext.add(applicationContext);
    }


    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        return new MergeableBeanFactory(mergedContext);
    }
}
