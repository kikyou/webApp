package org.baidu.ecom.eclipseWebApp;

/**
 * Created by baidu on 2017/1/13.
 */

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class FilterServletContext {

    public static class HelloServlet extends HttpServlet {

        public HelloServlet() {
            super();
        }

        public HelloServlet(String name) {
            super();
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        }
    }
    /**
     * @param args
     * Administrator
     * 2012-9-12 下午1:12:10
     */
    public static void main(String[] args) throws Exception{
// TODO Auto-generated method stub
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new HelloServlet()), "/*");
        context.addServlet(new ServletHolder(new HelloServlet("Buongiorno Mondo")), "/it/*");
        context.addServlet(new ServletHolder(new HelloServlet("Bonjour le monde")), "/fr/*");

        context.addFilter(HelloPrintingFilter.class, "/*",EnumSet.of(DispatcherType.REQUEST));

        server.start();
        server.join();
    }

    public static class HelloPrintingFilter implements Filter {

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            System.out.print("hello from filter");
            chain.doFilter(request, response);
        }

        public void init(FilterConfig arg0) throws ServletException {

        }

        public void destroy() {}
    }
}