package org.jasig.cas.saml

import org.jasig.cas.authentication.handler.AuthenticationHandler
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver
import org.jasig.cas.authentication.saml.SpringSecuritySamlAuthenticationHandler
import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentialsToPrincipalResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import javax.annotation.PostConstruct

class AuthenticationManagerConfigurer {
    @Autowired
    @Qualifier("authenticationHandlers")
    List<AuthenticationHandler> authenticationHandlers

    @Autowired
    @Qualifier("credentialsToPrincipalResolvers")
    List<CredentialsToPrincipalResolver> credentialsToPrincipalResolvers

    @Autowired(required = false)
    SpringSecuritySamlAuthenticationHandler springSecuritySamlAuthenticationHandler

    @Autowired(required = false)
    SpringSecuritySamlCredentialsToPrincipalResolver springSecuritySamlCredentialsToPrincipalResolver

    @PostConstruct
    def setup() {
        assert authenticationHandlers, "authenticationHandlers cannot be null"
        assert credentialsToPrincipalResolvers, "credentialsToPrincipalResolvers"

        authenticationHandlers.add(springSecuritySamlAuthenticationHandler ?: new SpringSecuritySamlAuthenticationHandler())
        credentialsToPrincipalResolvers.add(springSecuritySamlCredentialsToPrincipalResolver ?: new SpringSecuritySamlCredentialsToPrincipalResolver())
    }
}
