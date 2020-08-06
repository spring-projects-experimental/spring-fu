package com.sample

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ConstraintViolations
import am.ik.yavi.fn.Either

data class User(
        val login: String?,
        val firstname: String,
        val lastname: String
) {
    companion object {
        val validator = ValidatorBuilder.of<User>()
                .konstraint(User::login) {
                    notNull().greaterThanOrEqual(4).lessThanOrEqual(8)
                }
                .konstraint(User::firstname) {
                    notBlank().lessThanOrEqual(32)
                }
                .konstraint(User::lastname) {
                    notBlank().lessThanOrEqual(32)
                }
                .build()
    }

    fun validate(): Either<ConstraintViolations, User> {
        return validator.validateToEither(this)
    }
}