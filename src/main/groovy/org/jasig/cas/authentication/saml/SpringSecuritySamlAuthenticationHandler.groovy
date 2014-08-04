package org.jasig.cas.authentication.saml

import org.jasig.cas.authentication.handler.AuthenticationException
import org.jasig.cas.authentication.handler.NamedAuthenticationHandler
import org.jasig.cas.authentication.principal.Credentials
import org.jasig.cas.service.PersonService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * {@link org.jasig.cas.authentication.handler.AuthenticationHandler} used to authenticate {@link SpringSecuritySamlCredentials}
 *
 * @author Dmitriy Kopylenko
 * @author JJ
 * @author Unicon, inc.
 */
class SpringSecuritySamlAuthenticationHandler implements NamedAuthenticationHandler {
    PersonService personService
    private static final Logger logger = LoggerFactory.getLogger(SpringSecuritySamlAuthenticationHandler)

    @Override
    String getName() {
        this.class.name
    }

    @Override
    boolean authenticate(Credentials credentials) throws AuthenticationException {
        def username = ((SpringSecuritySamlCredentials)credentials).samlPrincipalId
        if (!username) {
            return false
        }
        if (!personService.findPersonByExternalIdAndGroup(username, ((SpringSecuritySamlCredentials)credentials).samlGroup)) {
            logger.info(String.format("User %s not found, authentication failed", username));
            return false
        }
        return true
    }

    @Override
    boolean supports(Credentials credentials) {
        return credentials == null ? false : SpringSecuritySamlCredentials.isAssignableFrom(credentials.class)
    }
}
