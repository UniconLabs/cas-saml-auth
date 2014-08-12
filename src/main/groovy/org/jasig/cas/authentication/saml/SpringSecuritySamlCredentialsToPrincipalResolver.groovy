package org.jasig.cas.authentication.saml

import org.jasig.cas.authentication.principal.AbstractPersonDirectoryCredentialsToPrincipalResolver
import org.jasig.cas.authentication.principal.Credentials
import org.jasig.cas.service.IdpService
import org.springframework.beans.factory.annotation.Autowired

/**
 * {@link org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver} for {@link SpringSecuritySamlCredentials}
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class SpringSecuritySamlCredentialsToPrincipalResolver extends AbstractPersonDirectoryCredentialsToPrincipalResolver {
    @Autowired
    IdpService idpService

    @Override
    protected String extractPrincipalId(Credentials credentials) {
        def p = idpService.extractPrincipalId(credentials)
        return p
    }

    @Override
    boolean supports(Credentials credentials) {
        return credentials == null ? false : SpringSecuritySamlCredentials.isAssignableFrom(credentials.class)
    }
}
