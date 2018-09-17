package org.springframework.boot.kofu.samples

import org.springframework.boot.kofu.application

private fun beansDsl() {
	application {
		beans {
			bean<UserRepository>()
			bean<ArticleRepository>()
			bean<HtmlHandler>()
			bean<ApiHandler>()
		}
	}
}
