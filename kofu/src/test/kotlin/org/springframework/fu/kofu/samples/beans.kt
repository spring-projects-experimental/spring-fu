package org.springframework.fu.kofu.samples

import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application

private fun beansDsl() {
	application(WebApplicationType.NONE) {
		beans {
			bean<UserRepository>()
			bean<ArticleRepository>()
			bean<HtmlHandler>()
			bean<ApiHandler>()
		}
	}
}

