package com.mercadolivro.exceptions

class BadRequestException(override val message: String, val errorCode: String): Exception() {
}