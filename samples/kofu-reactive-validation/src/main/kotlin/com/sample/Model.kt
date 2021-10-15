package com.sample

import am.ik.yavi.builder.validator
import am.ik.yavi.core.Validated

data class User(
        val login: String?,
        val firstname: String,
        val lastname: String
) {
    companion object {
        val validator = validator<User> {
            User::login {
                notNull()
                greaterThanOrEqual(4)
                lessThanOrEqual(8)
            }
            User::firstname {
                notBlank()
                lessThanOrEqual(32)
            }
            User::lastname {
                notBlank()
                lessThanOrEqual(32)
            }
        }.applicative()
    }

    fun validate(): Validated<User> {
        return validator.validate(this)
    }
}
