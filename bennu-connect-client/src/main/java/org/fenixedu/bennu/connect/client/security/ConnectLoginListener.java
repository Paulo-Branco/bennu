package org.fenixedu.bennu.connect.client.security;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.UserAuthenticationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

public class ConnectLoginListener implements UserAuthenticationListener {

    @Override
    public boolean shouldAllowSession(HttpSession session) {
        return Bennu.getInstance().getAuthenticationEventSet().stream()
                .anyMatch(e -> e.getSessionId().equals(session.getId()));
    }
}
