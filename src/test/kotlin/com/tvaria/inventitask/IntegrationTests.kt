package com.tvaria.inventitask

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
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

    @Test
    fun `Assert that CSV with invalid date not imported`() {
        val exampleCsv = "account,date,beneficiary,comment,amount,currency\nA,2000-00-00,B,,2,USD"
        val file = MockMultipartFile("csvFile", "example.csv", "text/csv", exampleCsv.toByteArray())
        mockMvc.perform(multipart("/import", "csvFile").file(file))
            .andExpect(status().isBadRequest)
            .andExpect { r ->
                assertTrue(r.resolvedException is ResponseStatusException)
                assertEquals("400 BAD_REQUEST \"Invalid date format: use yyyy-MM-dd\"", r.resolvedException!!.message)
            }
    }

    @Test
    fun `Assert that CSV with invalid amount not imported`() {
        val exampleCsv = "account,date,beneficiary,comment,amount,currency\nA,2000-01-01,B,,-2.2k,USD"
        val file = MockMultipartFile("csvFile", "example.csv", "text/csv", exampleCsv.toByteArray())
        mockMvc.perform(multipart("/import", "csvFile").file(file))
            .andExpect(status().isBadRequest)
            .andExpect { r ->
                assertTrue(r.resolvedException is ResponseStatusException)
                assertEquals("400 BAD_REQUEST \"Invalid amount\"", r.resolvedException!!.message)
            }
    }

    @Test
    fun `Assert that CSV with invalid currency not imported`() {
        val exampleCsv = "account,date,beneficiary,comment,amount,currency\nA,2000-01-01,B,,2,ABC"
        val file = MockMultipartFile("csvFile", "example.csv", "text/csv", exampleCsv.toByteArray())
        mockMvc.perform(multipart("/import", "csvFile").file(file))
            .andExpect(status().isBadRequest)
            .andExpect { r ->
                assertTrue(r.resolvedException is ResponseStatusException)
                assertEquals("400 BAD_REQUEST \"Invalid currency: use ISO 4217 code\"", r.resolvedException!!.message)
            }
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