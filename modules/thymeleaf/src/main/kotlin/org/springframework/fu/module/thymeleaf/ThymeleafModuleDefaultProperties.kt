package org.springframework.fu.module.thymeleaf

import org.springframework.http.MediaType
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.thymeleaf.templatemode.TemplateMode
import java.nio.charset.Charset

data class ThymeleafModuleDefaultProperties(
        var enabled: Boolean = true,
        var cache: Boolean = false,
        var checkTemplate: Boolean = true,
        var checkTemplateLocation: Boolean = true,
        var enableSpringElCompiler: Boolean = false,
        var encoding: Charset = Charsets.UTF_8,
        var excludeViewNames: Array<String> = arrayOf(),
        var mode: TemplateMode = TemplateMode.HTML,
        var prefix: String = "classpath:/templates/",
        var reactive: ThymeleafModuleDefaultReactiveProperties = ThymeleafModuleDefaultReactiveProperties(),
        var servletContentType: MimeType = MimeTypeUtils.TEXT_HTML,
        var suffix: String = ".html",
        var templateResolverOrder: Int? = null,
        var viewNames: Array<String> = arrayOf()
)

data class ThymeleafModuleDefaultReactiveProperties(
        var chunkedModeViewNames: Array<String> = arrayOf(),
        var fullModeViewNames: Array<String> = arrayOf(),
        var maxChunkSize: Int = 0,
        var mediaTypes: List<MediaType> = mutableListOf(MediaType.TEXT_HTML)
)
