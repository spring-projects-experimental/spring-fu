package org.springframework.fu

import org.springframework.boot.WebApplicationType

interface WebApplicationTypeProvider {
    val webApplicationType: WebApplicationType?
}
