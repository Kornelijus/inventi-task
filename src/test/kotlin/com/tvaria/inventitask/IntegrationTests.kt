package com.tvaria.inventitask

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityManager

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @Autowired
    lateinit var mockMvc: org.springframework.test.web.servlet.MockMvc

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `Assert that OpenAPI Schema is accessible`() {
        mockMvc.perform(get("/api-docs.json"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("\"title\":\"OpenAPI definition\"")))
    }

    @Test
    fun `Assert that Account balances calculated correctly`() {
        mockMvc.perform(get("/balance").param("accountNumber", "01"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("\"EUR\":-0.5")))
    }

    @BeforeEach
    fun setup() {
        val transactions = listOf(
            BankTransaction(
                "01",
                LocalDate.parse("2001-01-01"),
                "02",
                ".",
                "2.5".toBigDecimal(),
                Currency.getInstance("EUR")
            ),
            BankTransaction(
                "02",
                LocalDate.parse("2002-01-01"),
                "01",
                "..",
                "2.0".toBigDecimal(),
                Currency.getInstance("EUR")
            ),
            BankTransaction(
                "03",
                LocalDate.parse("2003-01-01"),
                "02",
                "...",
                "10".toBigDecimal(),
                Currency.getInstance("USD")
            ),
        )

        transactions.forEach { entityManager.persist(it); entityManager.flush() }
    }
}