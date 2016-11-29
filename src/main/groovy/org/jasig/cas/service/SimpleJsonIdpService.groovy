package org.jasig.cas.service

import groovy.json.JsonSlurper
import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentials
import org.jasig.cas.domain.Idp
import org.opensaml.saml2.metadata.EntityDescriptor
import org.opensaml.saml2.metadata.provider.FilesystemMetadataProvider
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider
import org.opensaml.xml.parse.BasicParserPool
import org.opensaml.xml.parse.ParserPool
import org.opensaml.xml.parse.StaticBasicParserPool
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource

import javax.annotation.PostConstruct

class SimpleJsonIdpService implements IdpService {
    Resource idpFile

    @Autowired(required = false)
    ParserPool parserPool

    Map<String, Idp> codeMap = [:]

    @PostConstruct
    void setup() {
        assert idpFile, "idpFile cannot be null"

        if (!parserPool) {
            parserPool = new BasicParserPool()
            parserPool.namespaceAware = true
        }

        def json = new JsonSlurper().parse(idpFile.file)
        json.each { item ->
            def idp = new Idp(item)
            if (!idp.entityId) {
                def metadataUrl = new URL(idp.metadataUrl)
                def e = metadataUrl.getText()
                def entityId = parserPool.parse(metadataUrl.openStream()).documentElement.getAttribute("entityID")
                idp.entityId = entityId
            }
            codeMap[item.code] = idp
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
        idps.find {it.entityId == entityId}
    }

    @Override
    String extractPrincipalId(SpringSecuritySamlCredentials springSecuritySamlCredentials) {
        def samlCredential = springSecuritySamlCredentials.samlCredential
        def idp = getIdpByEntityId(samlCredential.remoteEntityID)
        def idAttribute = idp.principalAttribute ?: "principal"
        return (samlCredential.getAttribute(idAttribute) ?: samlCredential.attributes.find {
            it.friendlyName == idAttribute || it.name == idAttribute
        })?.DOM?.textContent.trim()
    }
}
