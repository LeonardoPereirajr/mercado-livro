package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class CustomerServiceTest{

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bCript: BCryptPasswordEncoder

    @InjectMockKs //iniciar injeção posterior de customerService
    @SpyK
    private lateinit var customerService: CustomerService

    @Test
    fun `should return all customers`(){

        val fakeCustomers = listOf(buildCustomer(),buildCustomer())

        every { customerRepository.findAll()} returns fakeCustomers

        val customers = customerService.getAll(null)

        assertEquals(fakeCustomers,customers)
        verify(exactly = 0) {customerRepository.findByNameContaining(any())}

    }

    @Test
    fun `should return customers when name is informed`(){

        val name = UUID.randomUUID().toString()
        val fakeCustomers = listOf(buildCustomer(),buildCustomer())

        every { customerRepository.findByNameContaining(name)} returns fakeCustomers

        val customers = customerService.getAll(name)

        assertEquals(fakeCustomers,customers)
        verify(exactly = 0) {customerRepository.findAll()}
        verify(exactly = 1) {customerRepository.findByNameContaining(name)}

    }

    @Test
    fun `should create customers and encrypt password`(){
        val initialPassword = Random.nextInt().toString()
        val fakeCustomer = buildCustomer(password = initialPassword)
        val fakePassword = UUID.randomUUID().toString()
        val fakeCustomerEncrypted = fakeCustomer.copy(password = fakePassword)


        every { customerRepository.save(fakeCustomerEncrypted)} returns fakeCustomer
        every { bCript.encode(initialPassword) } returns fakePassword

        customerService.create(fakeCustomer)

        verify(exactly = 1){customerRepository.save(fakeCustomerEncrypted)}
        verify(exactly = 1){bCript.encode(initialPassword)}

    }

    @Test
    fun `should return customer id`(){
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id = id)

        every { customerRepository.findById(id) } returns Optional.of(fakeCustomer)

        val customer = customerService.findById(id)

        assertEquals(fakeCustomer, customer)
        verify(exactly = 1){customerRepository.findById(id)}

    }

    @Test
    fun `should throw found when find by id`(){
        val id = Random.nextInt()

        every { customerRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<com.mercadolivro.exceptions.NotFoundException>{
            customerService.findById(id)
        }

        assertEquals("Customer [${id}] not exist", error.message)
        verify(exactly = 1){customerRepository.findById(id)}

    }

    @Test
    fun `should update customer`(){
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id=id)

        every { customerRepository.existsById(id) } returns true
        every { customerRepository.save(fakeCustomer) } returns fakeCustomer

        customerService.update(fakeCustomer)

        verify(exactly = 1){ customerRepository.save(fakeCustomer)}
        verify(exactly = 1){ customerRepository.existsById(id)}

    }

    @Test
    fun `should throw not found exception update customer`(){
        val id = Random.nextInt()
        val fakeCustomer = buildCustomer(id=id)

        every { customerRepository.existsById(id) } returns false
        every { customerRepository.save(fakeCustomer) } returns fakeCustomer

        val error = assertThrows<com.mercadolivro.exceptions.NotFoundException>{
            customerService.update(fakeCustomer)
        }

        assertEquals("Customer [${id}] not exist", error.message)

        verify(exactly = 0){ customerRepository.save(any())}
        verify(exactly = 1){ customerRepository.existsById(id)}

    }

//    @Test
//    fun `should delete customer`(){
//        val id = Random.nextInt()
//        val fakeCustomer = buildCustomer( id = id)
//        val expectedCustomer = fakeCustomer.copy(status = CustomerStatus.INATIVO)
//
//        every { customerService.findById(id) } returns fakeCustomer
//        every { customerRepository.save(expectedCustomer) } returns expectedCustomer
//        every { bookService.deleteByCustomer(fakeCustomer) } just runs
//
//        customerService.delete(id)
//
//        verify(exactly = 1){ bookService.deleteByCustomer(fakeCustomer)}
//        verify(exactly = 1){ customerRepository.save(expectedCustomer)}
//
//    }

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
