package org.fenixedu.bennu.connect.client.domain;

import org.fenixedu.bennu.core.domain.Bennu;

public class RevokedToken extends RevokedToken_Base {
    
    public RevokedToken() {
        super();
    }

    public RevokedToken(String tokenId) {
        super();
        setBennu(Bennu.getInstance());
        setTokenId(tokenId);
    }
}
