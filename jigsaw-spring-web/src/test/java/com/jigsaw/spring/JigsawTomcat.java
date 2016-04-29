package com.jigsaw.spring;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
public class JigsawTomcat extends Tomcat {

    public Context getWebappContext(String url, String path) {
        return getWebappContext(url, url, path);
    }

    public Context getWebappContext(String url, String name, String path) {
        Context ctx = new StandardContext();
        ctx.setName(name);
        ctx.setPath(url);
        ctx.setDocBase(path);

        ctx.addLifecycleListener(new DefaultWebXmlListener());

        ContextConfig ctxCfg = new ContextConfig();
        ctx.addLifecycleListener(ctxCfg);

        // prevent it from looking ( if it finds one - it'll have dup error )
        ctxCfg.setDefaultWebXml(noDefaultWebXmlPath());

        return ctx;
    }

    public Context addContext(Host host, Context context) {
        silence(getHost(), context.getPath());

        if (host != null) {
            host.addChild(context);
        } else {
            getHost().addChild(context);
        }

        return context;
    }

    private void silence(Host host, String ctx) {
        String base = "org.apache.catalina.core.ContainerBase.[default].[";
        if (host == null) {
            base += getHost().getName();
        } else {
            base += host.getName();
        }
        base += "].[";
        base += ctx;
        base += "]";
        Logger.getLogger(base).setLevel(Level.WARNING);
    }
}
