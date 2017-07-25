package com.jigsaw.web.spring;

import com.jigsaw.core.Jigsaw;
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

    private Jigsaw jigsaw;

    public void merge(MergeableApplicationContext applicationContext) {
        mergedContext.add(applicationContext);
    }

    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        MergeableBeanFactory beanFactory = new MergeableBeanFactory(mergedContext);

        //register core system beans as singletons so that they can be referenced from pieces
        if (jigsaw != null && !beanFactory.containsBeanDefinition("jigsaw")) {
            beanFactory.registerSingleton("jigsaw", jigsaw);
        }

        return beanFactory;
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

    public void setJigsaw(Jigsaw jigsaw) {
        this.jigsaw = jigsaw;
    }
}
