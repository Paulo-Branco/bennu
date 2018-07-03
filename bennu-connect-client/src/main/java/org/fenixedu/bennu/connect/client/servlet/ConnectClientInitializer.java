package org.fenixedu.bennu.connect.client.servlet;

import org.fenixedu.bennu.connect.client.ConnectLoginProvider;
import org.fenixedu.bennu.connect.client.security.ConnectLoginListener;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConnectClientInitializer implements ServletContextListener {

    @Override public void contextInitialized(ServletContextEvent sce) {
        PortalLoginServlet.registerProvider(new ConnectLoginProvider());
        Authenticate.addUserAuthenticationListener(new ConnectLoginListener());
    }

    @Override public void contextDestroyed(ServletContextEvent sce) {

    }
}
