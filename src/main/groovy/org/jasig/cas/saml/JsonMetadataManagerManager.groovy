package org.jasig.cas.saml

import groovy.util.logging.Slf4j
import org.jasig.cas.service.IdpService
import org.opensaml.saml2.metadata.EntityDescriptor
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider
import org.opensaml.xml.parse.ParserPool
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.saml.metadata.MetadataManager

import javax.annotation.PostConstruct

@Slf4j
class JsonMetadataManagerManager {
    MetadataManager metadataManager
    ParserPool parserPool

    @Autowired
    IdpService idpService

    @PostConstruct
    def setup() {
        log.trace("setting up metadata")

        assert metadataManager, "metadataManager cannot be null"
        assert parserPool, "parserPool cannot be null"
        assert idpService, "idpService cannot be null"

        idpService.idps.each { idp ->
            new HTTPMetadataProvider(idp.metadataUrl, 5000).with {
                it.parserPool = this.parserPool
                it.initialize()
                if (!idp.entityId) {
                    idp.entityId = ((EntityDescriptor)it.metadata).entityID
                }
                metadataManager.addMetadataProvider(it)
            }
        }
        metadataManager.refreshMetadata()

        log.trace("done setting up metadata")
    }
}
