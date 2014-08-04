package org.jasig.cas.web.flow

import org.jasig.cas.domain.Group
import org.jasig.cas.domain.SamlGroup
import org.jasig.cas.service.PersonService
import org.springframework.webflow.execution.RequestContext

/**
 * Spring Web Flow action state that determines what login path to take based on the organization or internal username discovery.
 *
 * @author JJ
 * @author Dmitriy Kopylenko
 * @author Unicon , inc.
 */
class LoginTypeDiscoveryAction {

    private PersonService personService

    final static String CUSTOMER_GROUP_ID_KEY = 'customerGroup'

    final static String DISCOVER_BY_ORG_EVENT = 'discoverByOrg'

    final static String DISCOVER_BY_USERNAME_EVENT = 'discoverByUsername'

    final static String ORG_NOT_FOUND_RESULT = 'orgNotFound'

    final static String EMPTY_USERNAME_RESULT = 'emptyUsername'

    final static String EMPTY_ORG_RESULT = 'emptyOrg'

    final static String IDP_RESULT = 'idp'

    final static String INTERNAL_RESULT = 'internal'

    LoginTypeDiscoveryAction(PersonService personService) {
        this.personService = personService
    }

    String getType(RequestContext context) {
        if (DISCOVER_BY_ORG_EVENT == context.currentEvent.id) {
            def orgKey = context.requestParameters.orgKey
            if (orgKey) {
                def customerGroup = this.personService.findGroupByDiscoveryKey(orgKey)
                if (customerGroup) {
                    return loginTypeDiscoveryResult(context, customerGroup)
                }
                context.requestScope.put('orgNotFound', true)
                return ORG_NOT_FOUND_RESULT
            }
            context.requestScope.put('emptyOrg', true)
            return EMPTY_ORG_RESULT
        }
        else {
            def username = context.requestParameters.username
            if (username) {
                def person = this.personService.findPersonByUsername(username)
                if (!person) {
                    return INTERNAL_RESULT
                }
                return loginTypeDiscoveryResult(context, person.group)
            }
            context.requestScope.put('emptyUsername', true)
            return EMPTY_USERNAME_RESULT
        }

    }

    private static loginTypeDiscoveryResult(RequestContext context, Group customerGroup) {
        context.flowScope.put(CUSTOMER_GROUP_ID_KEY, customerGroup.id)
        if (customerGroup instanceof SamlGroup) {
            context.requestScope.put('idpId', (customerGroup as SamlGroup).idp.idpId)
            return IDP_RESULT
        }
        return INTERNAL_RESULT
    }
}
