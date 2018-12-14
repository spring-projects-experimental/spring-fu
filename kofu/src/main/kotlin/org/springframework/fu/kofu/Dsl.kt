package org.springframework.fu.kofu

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext

@DslMarker
internal annotation class KofuMarker

@KofuMarker
interface Dsl : ApplicationContextInitializer<GenericApplicationContext>