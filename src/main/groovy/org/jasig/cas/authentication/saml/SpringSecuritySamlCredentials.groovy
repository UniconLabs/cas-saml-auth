package org.jasig.cas.authentication.saml

import groovy.transform.EqualsAndHashCode
import org.jasig.cas.authentication.principal.Credentials
import org.springframework.security.saml.SAMLCredential

/**
 * {@link Credentials} class wrapping an instance of {@link org.springframework.security.saml.SAMLCredential}
 *
 * @author Dmitriy Kopylenko
 * @author JJ
 * @author Unicon, inc.
 */
@EqualsAndHashCode
class SpringSecuritySamlCredentials implements Credentials {
    final SAMLCredential samlCredential

    SpringSecuritySamlCredentials(SAMLCredential samlCredential) {
        this.samlCredential = samlCredential
    }

    String getSamlPrincipalId() {
        Idp = idp
        return (this.samlCredential.getAttributeByName(this.samlGroup.externalIdAttribute) ?: this.samlCredential.attributes.find {
            it.friendlyName == this.samlGroup.externalIdAttribute
        })?.DOM?.textContent
    }
}
