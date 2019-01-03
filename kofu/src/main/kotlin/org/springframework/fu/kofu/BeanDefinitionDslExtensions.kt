package org.springframework.fu.kofu

import org.springframework.context.support.BeanDefinitionDsl


/**
 * Declare a bean definition using the given callable reference with 1 parameter
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any>
        BeanDefinitionDsl.bean(crossinline f: Function1<A, T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 2 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any>
        BeanDefinitionDsl.bean(crossinline f: Function2<A,B,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 3 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any>
        BeanDefinitionDsl.bean(crossinline f: Function3<A,B,C,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 4 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any>
        BeanDefinitionDsl.bean(crossinline f: Function4<A,B,C,D,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 5 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any>
        BeanDefinitionDsl.bean(crossinline f: Function5<A,B,C,D,E,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 6 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any>
        BeanDefinitionDsl.bean(crossinline f: Function6<A,B,C,D,E,F,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 7 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any>
        BeanDefinitionDsl.bean(crossinline f: Function7<A,B,C,D,E,F,G,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 8 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any>
        BeanDefinitionDsl.bean(crossinline f: Function8<A,B,C,D,E,F,G,H,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 9 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any>
        BeanDefinitionDsl.bean(crossinline f: Function9<A,B,C,D,E,F,G,H,I,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 10 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any>
        BeanDefinitionDsl.bean(crossinline f: Function10<A,B,C,D,E,F,G,H,I,J,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 11 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any>
        BeanDefinitionDsl.bean(crossinline f: Function11<A,B,C,D,E,F,G,H,I,J,K,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 12 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any>
        BeanDefinitionDsl.bean(crossinline f: Function12<A,B,C,D,E,F,G,H,I,J,K,L,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 13 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any>
        BeanDefinitionDsl.bean(crossinline f: Function13<A,B,C,D,E,F,G,H,I,J,K,L,M,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 14 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any>
        BeanDefinitionDsl.bean(crossinline f: Function14<A,B,C,D,E,F,G,H,I,J,K,L,M,N,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 15 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any>
        BeanDefinitionDsl.bean(crossinline f: Function15<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 16 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any>
        BeanDefinitionDsl.bean(crossinline f: Function16<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 17 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any, reified Q: Any>
        BeanDefinitionDsl.bean(crossinline f: Function17<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 18 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any, reified Q: Any, reified R: Any>
        BeanDefinitionDsl.bean(crossinline f: Function18<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(),ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 19 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any, reified Q: Any, reified R: Any, reified S: Any>
        BeanDefinitionDsl.bean(crossinline f: Function19<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(),ref(),ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 20 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any, reified Q: Any, reified R: Any, reified S: Any, reified U: Any>
        BeanDefinitionDsl.bean(crossinline f: Function20<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,U,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(),ref(),ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 21 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any, reified Q: Any, reified R: Any, reified S: Any, reified U: Any,
        reified V: Any>
        BeanDefinitionDsl.bean(crossinline f: Function21<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,U,V,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(),ref(),ref(), ref(), ref())
    }
}

/**
 * Declare a bean definition using the given callable reference with 22 parameters
 * autowired by type for obtaining a new instance.
 * @see BeanDefinitionDsl.bean
 */
inline fun <reified T: Any, reified A: Any, reified B: Any, reified C: Any, reified D: Any, reified E: Any, reified F: Any,
        reified G: Any, reified H: Any, reified I: Any, reified J: Any, reified K: Any, reified L: Any, reified M: Any,
        reified N: Any, reified O: Any, reified P: Any, reified Q: Any, reified R: Any, reified S: Any, reified U: Any,
        reified V: Any, reified W: Any>
        BeanDefinitionDsl.bean(crossinline f: Function22<A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,U,V,W,T>,
                               name: String? = null,
                               scope: BeanDefinitionDsl.Scope? = null,
                               isLazyInit: Boolean? = null,
                               isPrimary: Boolean? = null,
                               isAutowireCandidate: Boolean? = null,
                               initMethodName: String? = null,
                               destroyMethodName: String? = null,
                               description: String? = null,
                               role: BeanDefinitionDsl.Role? = null) {

    bean(name, scope, isLazyInit, isPrimary, isAutowireCandidate, initMethodName, destroyMethodName, description, role) {
        f.invoke(ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(), ref(),ref(),ref(), ref(), ref(), ref())
    }
}