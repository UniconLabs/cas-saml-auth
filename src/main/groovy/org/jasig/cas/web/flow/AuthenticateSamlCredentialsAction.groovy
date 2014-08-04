package org.jasig.cas.web.flow

import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException
import org.jasig.cas.authentication.principal.Credentials
import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentials
import org.springframework.binding.message.MessageBuilder
import org.springframework.webflow.execution.RequestContext

/**
 * Action state for non-interactive authentication step in the regular CAS authentication process.
 * Invoked by the CAS' login flow execution machinery after an external IDP authentication leg has been performed
 * and external IDP principal has been successfully mapped to a native CAS Credentials (SpringSecuritySamlCredentials)
 * in this case.
 *
 * @author Dmitriy Kopylenko
 * @author Unicon , inc.
 * @see AbstractNonInteractiveCredentialsAction
 */
class AuthenticateSamlCredentialsAction extends AbstractNonInteractiveCredentialsAction {

    @Override
    protected Credentials constructCredentialsFromRequest(RequestContext context) {
        //Since we could only get here if we are already authenticated with external SAML IDP
        //and placed Credentials into the flow scope, we just get it from there.
        //This could result in a NPE and/or ClassCastException if this action state is misplaced within the login flow
        // or for some reason the Credentials in question did not make it to the flow scope at this stage in the processing.
        // Well, this is a catastrophic Runtime error that would need to be tracked down and debugged manually. At this point it's unrecoverable,
        //so no aggressive exception handling code is present. We'll treat it as a "kernel panic" here :-)
        return context.flowScope.get(SamlCredentialAdaptingAction.CREDENTIALS_KEY) as SpringSecuritySamlCredentials
    }

    @Override
    protected void onError(RequestContext context, Credentials credentials) {
        context.messageContext.addMessage(
                new MessageBuilder()
                        .error()
                        .code(BadCredentialsAuthenticationException.CODE)
                        .defaultText(BadCredentialsAuthenticationException.CODE).build())
    }
}
