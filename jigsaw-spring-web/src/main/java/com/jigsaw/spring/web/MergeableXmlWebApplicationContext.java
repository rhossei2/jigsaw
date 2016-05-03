package com.jigsaw.spring.web;

import com.jigsaw.spring.MergeableApplicationContext;
import com.jigsaw.spring.MergeableBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
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

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        super.refresh();

        getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this);
    }

    @Override
    public void close() {
        super.close();

        getServletContext().removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }
}
