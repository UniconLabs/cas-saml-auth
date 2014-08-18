package org.jasig.cas.web.flow

import org.jasig.cas.service.IdpService
import org.springframework.beans.factory.annotation.Autowired

class SamlAction {
    @Autowired
    IdpService idpService

    String getIdpIdFromCode(String code) {
        return idpService.getIdp(code).entityId
    }
}
