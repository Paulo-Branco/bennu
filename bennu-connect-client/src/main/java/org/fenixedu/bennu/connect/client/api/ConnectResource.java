package org.fenixedu.bennu.connect.client.api;

import bean.RevocationListBean;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.fenixedu.bennu.connect.client.ConnectClientConfiguration;
import org.fenixedu.bennu.connect.client.domain.AuthenticationEvent;
import org.fenixedu.bennu.connect.client.jwt.ConnectSigningKeyResolver;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.servlet.PortalLoginServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Path("/connect-client/login")
public class ConnectResource {

    private static Logger logger = LoggerFactory.getLogger(ConnectResource.class);

    private static final JwtParser JWT_PARSER = Jwts.parser().setSigningKeyResolver(new ConnectSigningKeyResolver());

    @GET
    @Path("/{callback}")
    public Response returnFromConnect(@QueryParam("token") String token, @PathParam("callback") String callback,
            @Context HttpServletRequest request, @Context HttpServletResponse response) throws URISyntaxException {


        if(!ConnectClientConfiguration.getConfiguration().connectEnabled()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // We should always have a ticket here, so fail fast if not
        if (Strings.isNullOrEmpty(token)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Check the callback is valid
        Optional<String> cb = decode(callback).filter(PortalLoginServlet::validateCallback);
        if (!cb.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String actualCallback = cb.get();

        try {
            // Begin by logging out
            Authenticate.logout(request, response);

            // Validate the ticket
            Claims claims = validate(token);
            String username = claims.getSubject();
            User user = getUser(username);
            Authenticate.login(request, response, user, "Connect Authentication");
            createAuthenticationEvent(claims.getId(), request.getSession());
            logger.trace("Logged in user {}, redirecting to {}", username, actualCallback);

        } catch (JwtException e) {
            logger.debug(e.getMessage(), e);
            // Append the login_failed parameter to the callback
            actualCallback = actualCallback + (actualCallback.contains("?") ? "&" : "?") + "login_failed=true";
        }

        return Response.status(Response.Status.FOUND).location(new URI(actualCallback)).build();
    }

    @POST
    @Path("/revokeHandler")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleTokenRevocationNotice(RevocationListBean revocationList) {
        logger.warn("Received revoke notice for tokens: ");
        revocationList.getTokenIds().forEach(token -> logger.warn(token));
        revocationList.getTokenIds().stream()
                .flatMap(t -> Bennu.getInstance().getAuthenticationEventSet().stream().filter(e -> e.getTokenId().equals(t)))
                .forEach(AuthenticationEvent::invalidate);
        return Response.ok().build();
    }

    @Atomic
    private void createAuthenticationEvent(String tokenId, HttpSession session) {
        new AuthenticationEvent(tokenId, session.getId());
    }

    private static Optional<String> decode(String base64Callback) {
        try {
            return Optional.of(new String(Base64.getUrlDecoder().decode(base64Callback), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            // Invalid Base64, return an empty Optional
            return Optional.empty();
        }
    }

    private Claims validate(String ticket) {
        // Decode the JWT
        return JWT_PARSER.parseClaimsJws(ticket).getBody();
    }

    private User getUser(String username) {
        User user = User.findByUsername(username);
        if (user == null) {
            user = attemptBootstrapUser(username);
        }
        return user;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private static User attemptBootstrapUser(String username) {
        User user = User.findByUsername(username);
        if (user != null) {
            return user;
        }
        logger.info("Created new user for {}", username);
        return new User(username, new UserProfile("Unknown", "User", null, null, null));
    }
}
