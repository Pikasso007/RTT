package com.rtt.transfer;

import com.rtt.transfer.configuration.ResourceLoader;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TransferBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(TransferBootstrap.class);

    private static final int TOMCAT_PORT = 8080;
    private static final String JERSEY_SERVLET_NAME = "jersey-servlet";

    public static void main(String[] args) throws Exception {
        startEmbeddedServer();
    }

    private static void startEmbeddedServer() throws LifecycleException {
        LOG.info("Starting embedded server");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(TOMCAT_PORT);

        Context context = tomcat.addContext("/", new File(".").getAbsolutePath());

        Tomcat.addServlet(context, JERSEY_SERVLET_NAME, resourceConfig());
        context.addServletMapping("/rest/*", JERSEY_SERVLET_NAME);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static ServletContainer resourceConfig() {
        return new ServletContainer(new ResourceConfig(new ResourceLoader().getClasses()));
    }

}
