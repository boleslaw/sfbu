package com.boleslaw;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.*;
public class JettyServer 
{
	public void start() throws Exception
	{
        Server server = new Server(new org.eclipse.jetty.util.thread.QueuedThreadPool(30, 30));
        ServerConnector cc = new ServerConnector(server);
        cc.setPort(1111);
        server.addConnector(cc);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", MediaServlet.class.getCanonicalName());
        server.setHandler(context);
        server.start();
        System.out.println(server.getThreadPool().getThreads());
        server.join();
	}
}
