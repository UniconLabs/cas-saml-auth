package org.jasig.cas.authentication.saml

import org.jasig.cas.authentication.principal.AbstractPersonDirectoryCredentialsToPrincipalResolver
import org.jasig.cas.authentication.principal.Credentials
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver
import org.jasig.cas.authentication.principal.Principal
import org.jasig.cas.authentication.principal.SimplePrincipal
import org.jasig.cas.service.IdpService
import org.springframework.beans.factory.annotation.Autowired

/**
 * {@link org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver} for {@link SpringSecuritySamlCredentials}
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class SpringSecuritySamlCredentialsToPrincipalResolver implements CredentialsToPrincipalResolver {
    @Autowired
    IdpService idpService

    @Override
    Principal resolvePrincipal(Credentials credentials) {
        def p = idpService.extractPrincipalId(credentials)
        if (!p) {
            return null
        }

        def attributes = ((SpringSecuritySamlCredentials)credentials).samlCredential.attributes.collectEntries {
            [(it.friendlyName ?: it.name): (it.attributeValues.size() == 1? it.attributeValues.get(0).DOM?.textContent.trim() : it.attributeValues.collect {it.DOM?.textContent.trim()})]
        }
        return new SimplePrincipal(p, attributes)
    }

    @Override
    boolean supports(Credentials credentials) {
        return credentials == null ? false : SpringSecuritySamlCredentials.isAssignableFrom(credentials.class)
    }
}
