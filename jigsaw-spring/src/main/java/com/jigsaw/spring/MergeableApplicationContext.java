package com.jigsaw.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class MergeableApplicationContext extends ClassPathXmlApplicationContext {

    private MergeableBeanFactory beanFactory;

    public MergeableApplicationContext() {
        beanFactory = new MergeableBeanFactory();
    }

    public MergeableApplicationContext(MergeableApplicationContext... contexts) {
        beanFactory = new MergeableBeanFactory(contexts);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        return beanFactory;
    }

    public BeanDefinition getBeanDefinition(String name) {
        return beanFactory.getBeanDefinition(name);
    }
}
