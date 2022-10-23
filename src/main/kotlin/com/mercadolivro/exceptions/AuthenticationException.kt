package com.mercadolivro.exceptions

class AuthenticationException(override val message: String, val errorCode: String): Exception() {
}