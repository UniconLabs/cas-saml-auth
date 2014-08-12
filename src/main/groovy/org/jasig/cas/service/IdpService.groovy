package org.jasig.cas.service

import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentials
import org.jasig.cas.domain.Idp

interface IdpService {
    Idp getIdp(String code)
    Set<Idp> getIdps()
    Idp getIdpByEntityId(String entityId)
    String extractPrincipalId(SpringSecuritySamlCredentials springSecuritySamlCredentials)
}
