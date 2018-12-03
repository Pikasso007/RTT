package com.rtt.transfer.services;

import com.rtt.transfer.services.configuration.RestResourcesConfig;
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

    private static final String JERSEY_SERVLET_NAME = "jersey-servlet";
    private static final String MAIN_PERSISTENCE_UNIT_NAME = "rtt";

    public static final int TOMCAT_PORT = 8080;

    private static Tomcat tomcat;

    public static void main(String[] args) throws Exception {
        ServicesManager.initServices(MAIN_PERSISTENCE_UNIT_NAME);

        startEmbeddedServer();
        tomcat.getServer().await();
    }

    public static void startEmbeddedServer() throws LifecycleException {
        LOG.info("Starting embedded server");

        tomcat = new Tomcat();
        tomcat.setPort(TOMCAT_PORT);

        Context context = tomcat.addContext("/", new File(".").getAbsolutePath());

        Tomcat.addServlet(context, JERSEY_SERVLET_NAME, resourceConfig());
        context.addServletMapping("/rest/*", JERSEY_SERVLET_NAME);

        tomcat.start();
    }

    public static void stopEmbeddedServer() throws LifecycleException {
        LOG.info("Stopping embedded server");
        tomcat.stop();
        tomcat.destroy();
    }

    private static ServletContainer resourceConfig() {
        return new ServletContainer(new ResourceConfig(new RestResourcesConfig().getClasses()));
    }

}
