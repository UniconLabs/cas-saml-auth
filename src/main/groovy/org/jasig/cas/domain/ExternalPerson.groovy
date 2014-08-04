package org.jasig.cas.domain

/**
 * Extension of {@link Person} for one that is provided by a third party
 */
class ExternalPerson extends Person {
    /**
     * External username/principal/etc
     */
    String externalId
}
