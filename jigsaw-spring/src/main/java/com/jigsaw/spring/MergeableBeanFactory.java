package com.jigsaw.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rhosseini
 * @date 4/19/2016
 */
public class MergeableBeanFactory extends DefaultListableBeanFactory {

    private static final Logger log = LoggerFactory.getLogger(MergeableBeanFactory.class);

    private List<MergeableApplicationContext> mergedBeanFactories = new ArrayList();

    public MergeableBeanFactory() {}

    public MergeableBeanFactory(MergeableApplicationContext... beanFactories) {
        for(MergeableApplicationContext beanFactory : beanFactories) {
            mergedBeanFactories.add(beanFactory);
        }
    }

    @Override
    public Object getBean(String name) throws BeansException {
        Object bean = getMergedBean(name);
        if(bean == null) {
            bean = super.getBean(name);
        }

        return bean;
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        Object bean = getMergedBean(name);
        if(bean == null) {
            bean = super.getBean(name, args);
        }

        return bean;
    }

    public Object getMergedBean(String beanName) {
        if(mergedBeanFactories.isEmpty()) {
            return null;
        }

        BeanDefinition localDefinition = getBeanDefinition(beanName);
        for(MergeableApplicationContext mergedBeanFactory : mergedBeanFactories) {
            if(mergedBeanFactory.containsBean(beanName)) {
                //if the beans were originated from the same source, use the already existing merged bean
                BeanDefinition mergedDefinition = mergedBeanFactory.getBeanDefinition(beanName);
                if(mergedDefinition.equals(localDefinition)) {
                    return mergedBeanFactory.getBean(beanName);
                }
            }
        }

        return null;
    }
}
