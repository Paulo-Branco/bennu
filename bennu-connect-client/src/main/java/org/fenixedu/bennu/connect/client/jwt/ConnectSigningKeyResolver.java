package org.fenixedu.bennu.connect.client.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import org.fenixedu.bennu.connect.client.domain.SigningKey;
import org.fenixedu.bennu.core.domain.Bennu;

import java.security.Key;

public class ConnectSigningKeyResolver implements SigningKeyResolver {
    @Override public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        return getKey(jwsHeader);
    }

    @Override public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
        return getKey(jwsHeader);
    }

    private Key getKey(JwsHeader header) {
        return Bennu.getInstance().getSigningKeySet().stream()
                    .filter(key -> key.getKeyId().equals(header.getKeyId()))
                    .findFirst()
                    .map(SigningKey::asPublicKey)
                    .orElseGet(null);
    }
}
