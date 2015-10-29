package com.delarosa.portal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author odelarosa
 */
public class ServerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // DB.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // DB.stop();
    }

}
