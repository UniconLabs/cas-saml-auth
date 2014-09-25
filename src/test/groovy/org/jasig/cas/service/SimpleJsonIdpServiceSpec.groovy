package org.jasig.cas.service

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.jasig.cas.authentication.saml.SpringSecuritySamlCredentials
import org.jasig.cas.domain.Idp
import org.opensaml.Configuration
import org.opensaml.DefaultBootstrap
import org.opensaml.saml2.core.Response
import org.opensaml.xml.parse.BasicParserPool
import org.springframework.core.io.ClassPathResource
import org.springframework.security.saml.SAMLCredential
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SimpleJsonIdpServiceSpec extends Specification {
    @Shared
    def idpService

    @Shared
    def idp

    @Shared
    def webserver

    def setupSpec() {
        DefaultBootstrap.bootstrap()
        idp = new Idp(
                code: 'SS',
                metadataUrl: 'http://localhost:22222/idp/profile/Metadata/SAML',
                principalAttribute: 'principal',
                entityId: 'https://test.scaldingspoon.org/idp/shibboleth'
        )
    }

    def setup() {
        webserver = HttpServer.create(new InetSocketAddress(22222), 0).with {
            it.createContext('/idp/profile/Metadata/SAML', new HttpHandler() {
                @Override
                void handle(HttpExchange httpExchange) throws IOException {
                    httpExchange.with {
                        responseHeaders.set("Content-Type", "text/plain")
                        def out = responseBody.newPrintWriter()
                        out << new ClassPathResource('idp-metadata.xml').inputStream
                        sendResponseHeaders(200, 0)
                        out.close()
                        responseBody.close()
                    }
                }
            })
            it.start()
            it
        }
        idpService = new SimpleJsonIdpService(idpFile: new ClassPathResource("idps.json")).with {
            it.setup()
            it
        }
    }

    def cleanup() {
        webserver.stop(0)
    }

    def "test setup"() {
        def service = new SimpleJsonIdpService(idpFile: new ClassPathResource("idps.json")).with {
            it.setup()
            it
        }
        expect:
        service.codeMap == ['SS': idp]
    }

    def "test getIdp"() {
        expect:
        idp == idpService.getIdp('SS')
    }

    def "test getIdpByEntityId"() {
        expect:
        idp == idpService.getIdpByEntityId('https://test.scaldingspoon.org/idp/shibboleth')
    }

    @Unroll
    def "test extract principal Id"() {
        def parserPool = new BasicParserPool()
        parserPool.namespaceAware = true

        def unmarshallerFactory = Configuration.unmarshallerFactory

        expect:
        def root = parserPool.parse(new ClassPathResource(b).inputStream).documentElement
        def unmarshaller = unmarshallerFactory.getUnmarshaller(root)

        Response e = unmarshaller.unmarshall(root)
        e.assertions[0].subject.nameID
        def credential = new SAMLCredential(
                e.assertions[0].subject.nameID,
                e.assertions[0],
                'https://test.scaldingspoon.org/idp/shibboleth',
                e.assertions[0].attributeStatements[0].attributes,
                'urn:test'
        )
        idpService.extractPrincipalId(new SpringSecuritySamlCredentials(credential)) == a
        where:
        a    | b
        'jj' | 'response.xml'
    }
}
