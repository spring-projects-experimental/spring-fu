package org.springframework.samples.petclinic

import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.paramOrNull
import java.time.LocalDate
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

// Because ... ¯\_(ツ)_/¯

inline fun <reified T : Any> ServerRequest.form(prefix: List<String> = listOf()): T =
        T::class.memberProperties
                .filterIsInstance(KMutableProperty::class.java)
                .fold(T::class.createInstance()) { instance, property ->
                    val value: Any? = paramOrNull((prefix + property.name).joinToString("."))?.asNotEmpty()?.let {
                        when (property.returnType) {
                            String::class, String::class.starProjectedType.withNullability(true) -> it
                            Int::class, Int::class.starProjectedType.withNullability(true) -> it.toInt()
                            Long::class, Long::class.starProjectedType.withNullability(true) -> it.toLong()
                            Double::class, Double::class.starProjectedType.withNullability(true) -> it.toDouble()
                            Boolean::class, Boolean::class.starProjectedType.withNullability(true) -> it.toBoolean()
                            LocalDate::class, LocalDate::class.starProjectedType.withNullability(true) -> LocalDate.parse(it)
                            // TODO manage collection & nested classes
                            else -> null
                        }
                    }
                    value?.let { property.setter.call(instance, it) }
                    instance
                }

fun String?.asNotEmpty(): String? = this?.let {
    if (it.isEmpty()) null else it
}