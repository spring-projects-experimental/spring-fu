/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.freemarker.freeMarker

/**
 * @see org.springframework.fu.kofu.webmvc.FreeMarkerDslTests
 */
fun freeMarkerDsl() {
    application(WebApplicationType.NONE) {
        freeMarker {
            settings["locale"] = "en_GB" // https://freemarker.apache.org/docs/api/freemarker/template/Configuration.html#setSetting-java.lang.String-java.lang.String-
            templateLoaderPath = arrayOf("classpath:/templates/", "classpath:/templates-2/")
        }
    }
}
