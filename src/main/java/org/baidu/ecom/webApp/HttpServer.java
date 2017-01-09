package org.baidu.ecom.webApp;

/**
 * Created by baidu on 2017/1/7.
 */

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/*
    learn to write java web server
    https://wiki.eclipse.org/Jetty/Tutorial/Jetty_and_Maven_HelloWorld
    https://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty

 */

public class HttpServer {

    Server server;
    Connector connector;
    WebAppContext webAppContext;
    Map<Context, Boolean> defaultContexts = new HashMap<Context, Boolean>();

    public String getId() {
        return "HttpServer-2017";
    }

    public HttpServer(String name) throws IOException {
        // set server
        server = new Server(8080);

        // set connector
        connector = createConnector();

        // init thread pool
        QueuedThreadPool qtp = new QueuedThreadPool();
        qtp.setMaxStopTimeMs(0);
        server.setThreadPool(qtp);

        // add handlers
        ContextHandlerCollection contexts = addHandler();

        // set web path
        final String appDir = getWebAppsPath();
        webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setWar(appDir + "/" + name);
        String[] welcomeFiles = new String[1];
        welcomeFiles[0] = "index.html";
        webAppContext.setWelcomeFiles(welcomeFiles);
        server.addHandler(webAppContext);

        //
        addDefaultApps(contexts, appDir);
    }

    public void setAttribute(String name, Object value) {
        webAppContext.setAttribute(name, value);
        // also add it to other contexts
        for (Context context : defaultContexts.keySet()) {
            context.setAttribute(name, value);
        }
    }

    public void start() throws IOException {
        connector.open();
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException("Problem starting http server", e);
        }
    }

    private void addDefaultApps(ContextHandlerCollection parent,
                                final String appDir) throws IOException {
        // set up the context for "/static/*"
        Context staticContext = new Context(parent, "/static");
        staticContext.setResourceBase(appDir + "/static");
        staticContext.addServlet(DefaultServlet.class, "/*");
        defaultContexts.put(staticContext, true);
    }

    private String getWebAppsPath() throws IOException {
        URL url = getClass().getClassLoader().getResource("webapps");
        if (url == null) {
            throw new IOException("webapps not found in CLASSPATH");
        }
        return url.toString();
    }

    private ContextHandlerCollection addHandler() {
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        RequestLogHandler requestLogHandler = new RequestLogHandler();
        File logDir = new File("./", "log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        NCSARequestLog requestLog = new NCSARequestLog(logDir.getAbsolutePath() + "/jetty-yyyy_mm_dd.request.log");
        requestLog.setRetainDays(30);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone(TimeZone.getDefault().getID());
        requestLogHandler.setRequestLog(requestLog);

        handlers.setHandlers(new Handler[]{contexts, requestLogHandler});
        server.setHandler(handlers);

        return contexts;
    }

    private Connector createConnector() {
        SelectChannelConnector ret = new SelectChannelConnector();
        ret.setLowResourceMaxIdleTime(10000);
        ret.setAcceptQueueSize(1024);
        ret.setResolveNames(false);
        ret.setUseDirectBuffers(false);
        return ret;
    }

    public static void main(String[] args) throws Exception {
        HttpServer httpServer = new HttpServer("mysql");

        httpServer.setAttribute("HttpServer", httpServer);
        httpServer.start();
    }
}
