package com.jigsaw.web.spring;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author rhosseini
 * @date 4/29/2016
 */
public interface MergeableApplicationContext extends ConfigurableApplicationContext {

    void merge(MergeableApplicationContext context);
}
