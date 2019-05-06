package com.sample

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.constraint
import am.ik.yavi.core.ConstraintViolations
import am.ik.yavi.fn.Either

data class User(
        val login: String,
        val firstname: String,
        val lastname: String
) {
    companion object {
        val validator = ValidatorBuilder.of<User>()
                .constraint(User::login) {
                    notNull().greaterThanOrEqual(4).lessThanOrEqual(8)
                }
                .constraint(User::firstname) {
                    notBlank().lessThanOrEqual(32)
                }
                .constraint(User::lastname) {
                    notBlank().lessThanOrEqual(32)
                }
                .build()
    }

    fun validate(): Either<ConstraintViolations, User> {
        return validator.validateToEither(this)
    }
}
