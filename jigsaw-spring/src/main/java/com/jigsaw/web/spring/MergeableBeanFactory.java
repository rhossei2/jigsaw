package com.jigsaw.web.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
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

    public MergeableBeanFactory(List<MergeableApplicationContext> mergedBeanFactories) {
        this.mergedBeanFactories = mergedBeanFactories;
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

        try {
            BeanDefinition localDefinition = getBeanDefinition(beanName);
            for(MergeableApplicationContext mergedBeanFactory : mergedBeanFactories) {
                if(mergedBeanFactory.containsBean(beanName)) {
                    /* if the beans were originated from the same source, use the already existing merged bean
                     * to avoid duplicate instances */
                    BeanDefinition mergedDefinition = mergedBeanFactory.getBeanFactory().getBeanDefinition(beanName);
                    if(mergedDefinition.equals(localDefinition)) {
                        return mergedBeanFactory.getBean(beanName);
                    }
                }
            }

        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }

        return null;
    }
}
