package org.jasig.cas.authentication.saml

import groovy.transform.EqualsAndHashCode
import org.jasig.cas.authentication.principal.Credentials
import org.jasig.cas.domain.SamlGroup
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

    private final SAMLCredential samlCredential

    SamlGroup samlGroup

    SpringSecuritySamlCredentials(SAMLCredential samlCredential) {
        this.samlCredential = samlCredential
    }

    String getSamlPrincipalId() {
        if (!this.samlCredential || !this.samlGroup) {
            // if one of these is null, return null
            return null
        }
        return (this.samlCredential.getAttributeByName(this.samlGroup.externalIdAttribute) ?: this.samlCredential.attributes.find {
            it.friendlyName == this.samlGroup.externalIdAttribute
        })?.DOM?.textContent
    }
}
