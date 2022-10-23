package com.mercadolivro.enums

enum class Errors(val code: String, val message: String) {
    ML101("ML-101", "Book [%s] not exist" ),
    ML102("ML-102", "Cannot update Book with status [%s]." ),
    ML201("ML-202", "Customer [%s] not exist" ),
    ML001("ML-001", "Invalid request")
}