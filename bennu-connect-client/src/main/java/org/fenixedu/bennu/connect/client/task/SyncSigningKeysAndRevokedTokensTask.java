package org.fenixedu.bennu.connect.client.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.connect.client.ConnectClientConfiguration;
import org.fenixedu.bennu.connect.client.domain.AuthenticationEvent;
import org.fenixedu.bennu.connect.client.domain.RevokedToken;
import org.fenixedu.bennu.connect.client.domain.SigningKey;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.rest.JsonBodyReaderWriter;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.lang.JoseException;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Task(englishTitle = "Syncs signing keys and revoked tokens with FenixEdu Connect")
public class SyncSigningKeysAndRevokedTokensTask extends CronTask {

    private static final Client HTTP_CLIENT = ClientBuilder.newBuilder().register(JsonBodyReaderWriter.class).build();
    private static final JsonParser parser = new JsonParser();

    private static final String connectServerUrl = ConnectClientConfiguration.getConfiguration().connectServerUrl();
    private static final String connectApiKey = ConnectClientConfiguration.getConfiguration().connectApiKey();

    @Override public void runTask() throws JoseException {
        JsonArray json = getKeyInformation();
        Map<String, PublicKey> keys = new HashMap<>();
        for (JsonElement key : json) {
            String keyId = key.getAsJsonObject().get("keyId").getAsString();
            PublicKey publicKey = PublicJsonWebKey.Factory.newPublicJwk(key.toString()).getPublicKey();
            keys.put(keyId, publicKey);
        }
        replaceKeys(keys);

        taskLog("Current key set:");
        Bennu.getInstance().getSigningKeySet().forEach(signingKey -> taskLog("Key ID: " + signingKey.getKeyId() + "\n" + signingKey.getPublicKey().toString()));

        List<String> revokedTokens = new ArrayList<>();
        getTokenInformation().forEach(token -> revokedTokens.add(token.toString()));
        replaceTokens(revokedTokens);

        taskLog("Current revoked tokens set:");
        Bennu.getInstance().getRevokedTokenSet().forEach(revokedToken -> taskLog(revokedToken.getTokenId()));

        Bennu.getInstance().getRevokedTokenSet().stream()
            .flatMap(t -> Bennu.getInstance().getAuthenticationEventSet().stream()
                                .filter(e -> e.getTokenId().equals(t.getTokenId())))
            .forEach(AuthenticationEvent::invalidate);
    }

    private JsonArray getKeyInformation() {
        return getInformation("keys").getAsJsonObject().getAsJsonArray("keys");
    }

    private JsonArray getTokenInformation() {
        return getInformation("token/revoked").getAsJsonArray();
    }

    private JsonElement getInformation(String endpoint) {
        Response response = HTTP_CLIENT.target(connectServerUrl).path("api/v1/sync").path(endpoint).request()
                .header("X-API-KEY", connectApiKey).get();
        if(response.getStatus() != 200) {
            taskLog("url errored and returned {}", response.getStatus());
        }

        return parser.parse(response.readEntity(String.class));
    }

    @Atomic(mode = TxMode.WRITE)
    private void replaceKeys(Map<String, PublicKey> keys) {
        Bennu.getInstance().getSigningKeySet().clear();
        keys.forEach(SigningKey::new);
    }

    @Atomic(mode = TxMode.WRITE)
    private void replaceTokens(List<String> tokens) {
        Bennu.getInstance().getRevokedTokenSet().clear();
        tokens.forEach(RevokedToken::new);
    }
}
