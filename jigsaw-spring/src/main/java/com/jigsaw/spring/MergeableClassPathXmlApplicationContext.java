package com.jigsaw.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class MergeableClassPathXmlApplicationContext extends ClassPathXmlApplicationContext
    implements MergeableApplicationContext {

    private List<MergeableApplicationContext> mergedContext = new ArrayList<>();

    public void merge(MergeableApplicationContext applicationContext) {
        mergedContext.add(applicationContext);
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(getClassLoader());

        super.refresh();

        Thread.currentThread().setContextClassLoader(classLoader);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        return new MergeableBeanFactory(mergedContext);
    }
}
