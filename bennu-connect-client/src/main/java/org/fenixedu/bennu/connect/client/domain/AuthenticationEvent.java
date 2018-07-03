package org.fenixedu.bennu.connect.client.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class AuthenticationEvent extends AuthenticationEvent_Base {
    
    public AuthenticationEvent() {
        super();
    }

    public AuthenticationEvent(String tokenId, String session) {
        super();
        setBennu(Bennu.getInstance());
        setTokenId(tokenId);
        setSessionId(session);
    }

    @Atomic(mode = TxMode.WRITE)
    public void invalidate() {
        setBennu(null);
        super.deleteDomainObject();
    }
}
