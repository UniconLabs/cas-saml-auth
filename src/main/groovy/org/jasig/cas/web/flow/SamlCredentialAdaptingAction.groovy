package org.jasig.cas.web.flow

import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentials
import org.jasig.cas.service.PersonService
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.saml.SAMLCredential
import org.springframework.webflow.execution.RequestContext

/**
 * Gets the <code>SAMLCredential</code> from an existing <code>SecurityContext#Authentication</code> object available in HttpSession
 * (which is assumed to be placed there by Spring Security SAML) and wraps it
 * with CAS' <code>Credentials</code> adapter and places it in the flow scope so it could be retrieved by downstream custom
 * <code>SpringSecuritySamlAuthenticationHandler</code>. Removes SecurityContext from HttpSession after SAMLCredential has been successfully
 * retrieved.
 * <p/>
 * Also checks if the Spring Security Authentication Exception is available in HttpSession under the standard Spring Security key of
 * <i>SPRING_SECURITY_LAST_EXCEPTION</i> and in this case wraps it with <code>ExternalSamlAuthenticationException</code> and then throws it,
 * so that login flow's global transition exception handler (if such is set up) has an opportunity to handle it, for example by transitioning
 * into a custom end-state, etc.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon, inc.
 */
class SamlCredentialAdaptingAction {
    private static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT"
    private static final String SPRING_SECURITY_LAST_EXCEPTION_KEY = "SPRING_SECURITY_LAST_EXCEPTION"

    static final String CREDENTIALS_KEY = "samlCredentials"

    PersonService personService

    public void wrapSamlCredentialAndPlaceInFlowScope(RequestContext context) {
        final def sessionMap = context.externalContext.sessionMap
        try {
            if (sessionMap.contains(SPRING_SECURITY_LAST_EXCEPTION_KEY)) {
                throw new ExternalSamlAuthenticationException(sessionMap.get(SPRING_SECURITY_LAST_EXCEPTION_KEY) as Throwable)
            }
            final def sc = sessionMap.get(SPRING_SECURITY_CONTEXT_KEY) as SecurityContext
            context.flowScope.put(
                    CREDENTIALS_KEY,
                    new SpringSecuritySamlCredentials(sc.authentication.credentials as SAMLCredential).with {
                        samlGroup = personService.getGroup(context.flowScope.get(LoginTypeDiscoveryAction.CUSTOMER_GROUP_ID_KEY))
                        it
                    }
            )
        }
        finally {
            sessionMap.remove(SPRING_SECURITY_CONTEXT_KEY)
            sessionMap.remove(SPRING_SECURITY_LAST_EXCEPTION_KEY)
        }
    }
}
