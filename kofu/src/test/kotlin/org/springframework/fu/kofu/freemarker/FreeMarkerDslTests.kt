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

package org.springframework.fu.kofu.freemarker

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils

/**
 * @author Ivan Skachkov
 */
class FreeMarkerDslTests {

    @Test
    fun `Use non web FreeMarker to generate content from template`() {
        val app = application(WebApplicationType.NONE) {
            beans { bean<FreeMarkerUser>() }
            freeMarker {
                templateLoaderPath = arrayOf("classpath:/templates/", "classpath:/templates-2/")
            }
        }

        with(app.run()) {
            val freeMarkerUser = getBean(FreeMarkerUser::class.java)
            assertEquals(freeMarkerUser.useTemplate(name = "template.ftlh"), "Hello world!")
            assertEquals(freeMarkerUser.useTemplate(name = "template-2.ftlh"), "Hello world, 2!")
            close()
        }
    }
}

class FreeMarkerUser(private val templateConfigurationFactory: FreeMarkerConfigurationFactoryBean) {
    fun useTemplate(name: String) = FreeMarkerTemplateUtils.processTemplateIntoString(
        templateConfigurationFactory.createConfiguration().getTemplate(name),
        mapOf("name" to "world")
    )
}
