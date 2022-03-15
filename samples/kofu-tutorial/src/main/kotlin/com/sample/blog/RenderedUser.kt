package com.sample.blog

data class RenderedUser(
    val id: Long,
    val login: String,
    val firstname: String,
    val lastname: String,
    val description: String?)

fun UserEntity.render() = RenderedUser(
    id.value,
    info.login.value,
    info.name.firstname,
    info.name.lastname,
    info.description)