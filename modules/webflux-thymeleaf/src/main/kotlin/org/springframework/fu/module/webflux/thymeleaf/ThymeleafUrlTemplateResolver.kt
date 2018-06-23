package org.springframework.fu.module.webflux.thymeleaf

import org.springframework.web.reactive.result.view.UrlBasedViewResolver
import org.thymeleaf.IEngineConfiguration
import org.thymeleaf.templateresolver.ITemplateResolver
import org.thymeleaf.templateresolver.TemplateResolution

class ThymeleafUrlTemplateResolver : UrlBasedViewResolver(), ITemplateResolver {
	override fun getName(): String {
		return this::class.java.name
	}
	override fun resolveTemplate(p0: IEngineConfiguration?, p1: String?, p2: String?, p3: MutableMap<String, Any>?): TemplateResolution? {
		return null
	}
}