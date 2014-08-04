package org.jasig.cas.authentication.saml

import org.jasig.cas.authentication.principal.AbstractPersonDirectoryCredentialsToPrincipalResolver
import org.jasig.cas.authentication.principal.Credentials
import org.jasig.cas.service.PersonService

/**
 * {@link org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver} for {@link SpringSecuritySamlCredentials}
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class SpringSecuritySamlCredentialsToPrincipalResolver extends AbstractPersonDirectoryCredentialsToPrincipalResolver {

    PersonService personService

    @Override
    protected String extractPrincipalId(Credentials credentials) {
        return personService.findPersonByCredentials((SpringSecuritySamlCredentials) credentials).username
    }

    @Override
    boolean supports(Credentials credentials) {
        return credentials == null ? false : SpringSecuritySamlCredentials.isAssignableFrom(credentials.class)
    }
}
