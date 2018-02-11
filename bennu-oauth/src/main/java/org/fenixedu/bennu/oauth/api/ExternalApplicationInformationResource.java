package org.fenixedu.bennu.oauth.api;

import com.google.gson.JsonElement;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.oauth.api.json.ExternalApplicationForUsersAdapter;
import org.fenixedu.bennu.oauth.domain.ApplicationUserSession;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;
import org.fenixedu.bennu.oauth.util.OAuthUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.Optional;

@Path("/bennu-oauth")
public class ExternalApplicationInformationResource extends BennuRestResource {

    @GET
    @Path("/info")
    public JsonElement getTokenIssuerInformation(@Context ContainerRequestContext requestContext) {

        String accessToken = OAuthUtils.getAccessToken(requestContext);
        Optional<ApplicationUserSession> session = OAuthUtils.extractUserSession(accessToken);
        if(session.isPresent()) {
            ExternalApplication externalApplication = session.get().getApplicationUserAuthorization().getApplication();
            return view(externalApplication, ExternalApplicationForUsersAdapter.class);
        }

        return null;
    }
}
