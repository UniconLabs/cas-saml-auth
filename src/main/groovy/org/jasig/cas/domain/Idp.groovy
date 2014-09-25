package org.jasig.cas.domain

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Idp implements Serializable {
    String entityId
    String metadataUrl
    String friendlyName
    String code
    String principalAttribute
}
