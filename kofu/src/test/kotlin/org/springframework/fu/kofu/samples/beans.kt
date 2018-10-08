package org.springframework.fu.kofu.samples

import org.springframework.context.support.beans
import org.springframework.fu.kofu.application

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
