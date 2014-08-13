package org.jasig.cas.authentication.saml

import org.jasig.cas.authentication.handler.AuthenticationException
import org.jasig.cas.authentication.handler.NamedAuthenticationHandler
import org.jasig.cas.authentication.principal.Credentials

/**
 * {@link org.jasig.cas.authentication.handler.AuthenticationHandler} used to authenticate {@link SpringSecuritySamlCredentials}
 *
 * @author Dmitriy Kopylenko
 * @author JJ
 * @author Unicon, inc.
 */
class SpringSecuritySamlAuthenticationHandler implements NamedAuthenticationHandler {
    @Override
    String getName() {
        this.class.name
    }

    @Override
    boolean authenticate(Credentials credentials) throws AuthenticationException {
        return true
    }

    @Override
    boolean supports(Credentials credentials) {
        return credentials == null ? false : SpringSecuritySamlCredentials.isAssignableFrom(credentials.class)
    }
}
