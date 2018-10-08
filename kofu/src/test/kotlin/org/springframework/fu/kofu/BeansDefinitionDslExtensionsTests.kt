package org.springframework.fu.kofu

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans

class BeansDefinitionDslExtensionsTests {

	@Test
	fun `Create a bean from a callable reference with 0 parameter`() {
		val beans = beans {
			bean(::fun0)
		}
		val context = GenericApplicationContext()
		beans.initialize(context)
		context.refresh()
		context.getBean<Foo>()
	}

	@Test
	fun `Create a bean from a callable reference with 1 parameter`() {
		val beans = beans {
			bean<A>()
			bean(::fun1)
		}
		val context = GenericApplicationContext()
		beans.initialize(context)
		context.refresh()
		context.getBean<Foo>()
	}

	@Test
	fun `Create a bean from a callable reference with 2 parameters`() {
		val beans = beans {
			bean<A>()
			bean<B>()
			bean(::fun2)
		}
		val context = GenericApplicationContext()
		beans.initialize(context)
		context.refresh()
		context.getBean<Foo>()
	}

	@Test
	fun `Create a bean from a callable reference with 3 parameters`() {
		val beans = beans {
			bean<A>()
			bean<B>()
			bean<C>()
			bean(::fun3)
		}
		val context = GenericApplicationContext()
		beans.initialize(context)
		context.refresh()
		context.getBean<Foo>()
	}

	@Test
	fun `Create a bean from a callable reference with 4 parameters`() {
		val beans = beans {
			bean<A>()
			bean<B>()
			bean<C>()
			bean<D>()
			bean(::fun4)
		}
		val context = GenericApplicationContext()
		beans.initialize(context)
		context.refresh()
		context.getBean<Foo>()
	}

	@Test
	fun `Create a bean from a callable reference with 5 parameters`() {
		val beans = beans {
			bean<A>()
			bean<B>()
			bean<C>()
			bean<D>()
			bean<E>()
			bean(::fun5)
		}
		val context = GenericApplicationContext()
		beans.initialize(context)
		context.refresh()
		context.getBean<Foo>()
	}
}

class A
class B
class C
class D
class E
@Suppress("UNUSED_PARAMETER")
class Foo(a: A? = null, b: B? = null, c: C? = null, d: D? = null, e: E? = null)

fun fun0() = Foo()
fun fun1(a: A) = Foo(a)
fun fun2(a: A, b: B) = Foo(a, b)
fun fun3(a: A, b: B, c: C) = Foo(a, b, c)
fun fun4(a: A, b: B, c: C, d: D) = Foo(a, b, c, d)
fun fun5(a: A, b: B, c: C, d: D, e: E) = Foo(a, b, c, d, e)


