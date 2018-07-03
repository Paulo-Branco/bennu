package org.fenixedu.bennu.connect.client;

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.login.LoginProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ConnectLoginProvider implements LoginProvider {
    private final Escaper escaper = UrlEscapers.urlPathSegmentEscaper();

    @Override
    public void showLogin(HttpServletRequest request, HttpServletResponse response, String callback) throws IOException {
        if (Strings.isNullOrEmpty(callback)) {
            callback = ConnectClientConfiguration.getConfiguration().connectServiceUrl();
        }
        callback = Base64.getUrlEncoder().encodeToString(callback.getBytes(StandardCharsets.UTF_8));
        response.sendRedirect(ConnectClientConfiguration.getConfiguration().connectServerUrl() + "/login?callback="
                + escaper.escape(CoreConfiguration.getConfiguration().applicationUrl() + "/api/connect-client/login/" + callback));
    }

    @Override
    public String getKey() {
        return "connect";
    }

    @Override
    public String getName() {
        return "Connect";
    }

    @Override
    public boolean isEnabled() {
        return ConnectClientConfiguration.getConfiguration().connectEnabled();
    }
}
