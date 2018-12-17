package org.springframework.fu.kofu.samples

import org.springframework.fu.kofu.application

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

private fun beanScanDsl() {
	application {
		beans {
			scan("com.example.beans")
		}
	}
}
