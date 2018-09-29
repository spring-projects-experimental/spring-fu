package org.springframework.boot.kofu

import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils.*
import org.springframework.context.support.BeanDefinitionDsl
import org.springframework.context.support.registerBean

inline fun <reified T: Any> BeanDefinitionDsl.bean(crossinline f: Function0<T>) {
	context.registerBean(uniqueBeanName(T::class.java.name, context)) { f.invoke() }
}

inline fun <reified A: Any, reified T: Any> BeanDefinitionDsl.bean(crossinline f: Function1<A, T>) {
	context.registerBean(uniqueBeanName(T::class.java.name, context)) { f.invoke(context.getBean()) }
}

inline fun <reified A: Any, reified B: Any, reified T: Any> BeanDefinitionDsl.bean(crossinline f: Function2<A, B, T>) {
	context.registerBean(uniqueBeanName(T::class.java.name, context)) { f.invoke(context.getBean(), context.getBean()) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified T: Any> BeanDefinitionDsl.bean(crossinline f: Function3<A, B, C, T>) {
	context.registerBean(uniqueBeanName(T::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean()) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified T: Any> BeanDefinitionDsl.bean(crossinline f: Function4<A, B, C, D, T>) {
	context.registerBean(uniqueBeanName(T::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean(), context.getBean()) }
}

inline fun <reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified T: Any> BeanDefinitionDsl.bean(crossinline f: Function5<A, B, C, D, E, T>) {
	context.registerBean(uniqueBeanName(T::class.java.name, context)) { f.invoke(context.getBean(), context.getBean(), context.getBean(), context.getBean(), context.getBean()) }
}
