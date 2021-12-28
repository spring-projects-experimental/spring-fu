package org.springframework.fu.kofu.samples

import de.flapdoodle.embed.mongo.distribution.Version
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.mongo.reactiveMongodb

fun mongo() {
	application {
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
		}
	}
}

fun mongoEmbedded() {
	application {
		reactiveMongodb {
			uri = "mongodb://myserver.com/foo"
			embedded(version = Version.Main.PRODUCTION)
		}
	}
}
