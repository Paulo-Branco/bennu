package org.fenixedu.bennu.connect.client.domain;

import org.fenixedu.bennu.core.domain.Bennu;

import java.io.Serializable;
import java.security.PublicKey;

public class SigningKey extends SigningKey_Base {
    
    public SigningKey() {
        super();
    }

    public SigningKey(String keyId, Serializable publicKey) {
        super();
        setBennuSigningKey(Bennu.getInstance());
        setKeyId(keyId);
        setPublicKey(publicKey);
    }

    public PublicKey asPublicKey() {
        return (PublicKey) this.getPublicKey();
    }
}
