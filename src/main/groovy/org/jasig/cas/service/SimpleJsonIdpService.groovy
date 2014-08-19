package org.jasig.cas.service

import groovy.json.JsonSlurper
import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentials
import org.jasig.cas.domain.Idp
import org.springframework.core.io.Resource

import javax.annotation.PostConstruct

class SimpleJsonIdpService implements IdpService {
    Resource idpFile
    // Set<Idp> idps = [] as Set

    Map<String, Idp> codeMap = [:]

    @PostConstruct
    void setup() {
        assert idpFile, "idpFile cannot be null"

        def json = new JsonSlurper().parse(idpFile.file)
        json.each { item ->
            codeMap[item.code] = new Idp(item)
        }
    }

    @Override
    Idp getIdp(String code) {
        idps.find {it.code == code}
    }

    @Override
    Set<Idp> getIdps() {
        return codeMap.values() as Set
    }

    @Override
    Idp getIdpByEntityId(String entityId) {
        idps.find {it.entityId = entityId}
    }

    @Override
    String extractPrincipalId(SpringSecuritySamlCredentials springSecuritySamlCredentials) {
        def samlCredential = springSecuritySamlCredentials.samlCredential
        def idp = getIdpByEntityId(samlCredential.remoteEntityID)
        def idAttribute = idp.principalAttribute ?: "principal"
        return (samlCredential.getAttribute(idAttribute) ?: samlCredential.attributes.find {
            it.friendlyName == idAttribute
        })?.DOM?.textContent
    }
}
