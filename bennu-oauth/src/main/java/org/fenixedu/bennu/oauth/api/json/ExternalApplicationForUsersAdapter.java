package org.fenixedu.bennu.oauth.api.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.oauth.domain.ExternalApplication;

public class ExternalApplicationForUsersAdapter extends ExternalApplicationAdapter {

    @Override
    public JsonElement view(ExternalApplication obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("name", obj.getName());
        json.addProperty("description", obj.getDescription());
        json.addProperty("siteUrl", obj.getSiteUrl());
        json.addProperty("active", obj.isActive());
        json.addProperty("redirectUrl", obj.getRedirectUrl());
        json.addProperty("author", obj.getAuthorApplicationName());
        json.add("scopes", ctx.view(obj.getScopesSet()));

        String logoUrl =
                CoreConfiguration.getConfiguration().applicationUrl() + "/api/bennu-oauth/applications/" + obj.getExternalId()
                        + "/logo";

        json.addProperty("logoUrl", logoUrl);

        return json;
    }
}
