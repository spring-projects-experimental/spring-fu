package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application
import org.springframework.context.support.beans

private fun beans() {
	val beans = beans {
		bean<UserRepository>()
		bean<ArticleRepository>()
		bean<HtmlHandler>()
		bean<ApiHandler>()
	}
	application {
		import(beans)
	}
}
