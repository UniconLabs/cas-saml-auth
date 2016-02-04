# Plugin for SAML 2.0 authentication in CAS

## Configuration

Add the following dependencies:

```
        <dependency>
            <groupId>org.apereo.cas</groupId>
            <artifactId>cas-saml-auth</artifactId>
            <version>1.0-M6</version>
        </dependency>
        <dependency>
            <groupId>org.opensaml</groupId>
            <artifactId>opensaml</artifactId>
            <version>2.5.3</version>
        </dependency>
```

The following settings in cas.properties:

```
####
# SAML Settings
####
saml.sp.alias=cas
saml.ssl.keystore.location=file:/opt/cas/samlKeystore.jks
saml.ssl.keystore.password=thisisasecurepasword
saml.ssl.keystore.key=cas
saml.ssl.keystore.key.password=thisisasecurepasword
saml.sp.metadata.location=file:/opt/cas/cas-sp-metadata-1.0.xml
saml.idps.file=file:///opt/cas/idps.json
```

Adjust in cas-servlet.xml:

```
 <webflow:flow-registry id="flowRegistry" flow-builder-services="builder">
    <webflow:flow-location path="/WEB-INF/login-webflow.xml" id="login"/>
      <webflow:flow-location path="classpath:META-INF/cas/spring/webflow/saml.xml" id="saml" />
  </webflow:flow-registry>
```

The following resolver to the deployerConfigContext.xml:

```
<bean class="org.jasig.cas.authentication.saml.SpringSecuritySamlCredentialsToPrincipalResolver" />
```

This handler:

```
<bean class="org.jasig.cas.authentication.saml.SpringSecuritySamlAuthenticationHandler" />
```

And the following snippet, all in the deployerConfigContext.xml file:

```
    <bean id="idpService"
          class="org.jasig.cas.service.SimpleJsonIdpService"
          p:idpFile="${saml.idps.file}" />
```

In the web.xml, adjust:

```
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            classpath*:META-INF/cas/spring/configuration/samlSecurityContext.xml
            /WEB-INF/spring-configuration/*.xml
            /WEB-INF/deployerConfigContext.xml
        </param-value>
    </context-param>
    
```

And in the same file, add the filter mapping:

```
    <!-- SAML front channel -->
    <servlet-mapping>
        <servlet-name>cas</servlet-name>
        <url-pattern>/saml/*</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>cas</servlet-name>
        <url-pattern>/idpAuthnFinishedCallback</url-pattern>
    </servlet-mapping>
    
    <filter>
        <filter-name>springSecurityFilterChain</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>springSecurityFilterChain</filter-name>
        <url-pattern>/saml/*</url-pattern>
    </filter-mapping>

    <!-- end SAML front channel -->
```

Right below the `springSecurityFilterChain` filter. 

Then, finally in your login-webflow:

```
 <action-state id="generateLoginTicket">
        <evaluate expression="generateLoginTicketAction.generate(flowRequestContext)" />
        <transition on="generated" to="goToSaml" />
    </action-state>

    <action-state id="goToSaml">
        <on-entry>
            <set name="conversationScope.idpCode" value="'XYZ'" />
        </on-entry>
        <evaluate expression="'samlSubflow'" />
        <transition on="samlSubflow" to="samlSubflow" />
    </action-state>

    <subflow-state id="samlSubflow" subflow="saml">
        <transition on="warn" to="warn" />
        <transition on="generateLoginTicket" to="generateLoginTicket" />
        <transition on="sendTicketGrantingTicket" to="sendTicketGrantingTicket" />
    </subflow-state>

```

You will need to modify that XYZ to match the code provided by the idps.json file.

Here is a sample file:

```
[
  {
    "code": "SS",
    "metadataUrl": "https://test.scaldingspoon.org/idp/profile/Metadata/SAML",
    "principalAttribute": "principal"
  }
]
```

Change the metadata url as you see fit, and change the code. 
