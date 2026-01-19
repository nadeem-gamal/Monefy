package com.monefy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer class to configure the application when it's deployed as a WAR.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(ServletInitializer.class);

    /**
     * Configures the application.
     *
     * @param application a builder for the application context
     * @return the application builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        logger.info("Initializing Servlet with MonefyApplication");
        return application.sources(MonefyApplication.class);
    }

}
