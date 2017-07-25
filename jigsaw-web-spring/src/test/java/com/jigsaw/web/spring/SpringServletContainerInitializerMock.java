package com.jigsaw.web.spring;

import com.jigsaw.web.spring.MergeableXmlWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class SpringServletContainerInitializerMock implements ServletContainerInitializer {

    private MergeableXmlWebApplicationContext context;

    public SpringServletContainerInitializerMock(MergeableXmlWebApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("This is initializing!");

        context.setServletContext(servletContext);
        context.setClassLoader(this.getClass().getClassLoader());
        context.refresh();

        //set the context as an attribute so Spring servletDispatcher can reference it
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }
}
