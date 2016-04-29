package com.jigsaw.spring.web;

import com.jigsaw.spring.MergeableApplicationContext;
import com.jigsaw.spring.MergeableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class MergeableXmlWebApplicationContext extends XmlWebApplicationContext
    implements MergeableApplicationContext {

    private List<MergeableApplicationContext> mergedContext = new ArrayList<>();

    public void merge(MergeableApplicationContext applicationContext) {
        mergedContext.add(applicationContext);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        return new MergeableBeanFactory(mergedContext);
    }
}
