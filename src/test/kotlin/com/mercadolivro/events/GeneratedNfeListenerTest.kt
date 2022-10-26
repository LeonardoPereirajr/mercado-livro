package com.mercadolivro.events

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.service.PurchaseService
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.model.PurchaseModel
import io.mockk.*
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
internal class GeneratedNfeListenerTest {

    @MockK
    private lateinit var purchaseService: PurchaseService

    @InjectMockKs
    private lateinit var generatedNfeListener: GeneratedNfeListener

    @Test
    fun `listen`() {
        val purchase = buildPurchase(nfe = null)
        val fakeNfe = UUID.randomUUID()
        val purchaseExpected = purchase.copy(nfe= fakeNfe.toString())
        mockkStatic(UUID::class)

        every { UUID.randomUUID() } returns fakeNfe
        every { purchaseService.update(purchaseExpected) } just runs

        generatedNfeListener.listen(PurchaseEvent(this, purchase))

        verify(exactly = 1){purchaseService.update(purchaseExpected)}
    }

    fun buildPurchase(
        id: Int? = null,
        customer: CustomerModel = buildCustomer(),
        books: MutableList<BookModel> = mutableListOf(),
        nfe: String? = UUID.randomUUID().toString(),
        price: BigDecimal = BigDecimal.TEN
    ) = PurchaseModel(
        id = id,
        customer = customer,
        books = books,
        price = price,
    )
    private fun buildCustomer(
        id: Int? = null,
        name: String = "customer name",
        email: String = "${UUID.randomUUID()}@email.com",
        password: String = "password"
    ) = CustomerModel(
        id = id,
        name = name,
        email = email,
        status = CustomerStatus.ATIVO,
        password = password,
        roles = setOf(Role.CUSTOMER)
    )
}