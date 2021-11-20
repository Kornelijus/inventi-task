package com.tvaria.inventitask

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate
import java.util.*

@DataJpaTest
class RepositoryTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val transactionRepository: TransactionRepository
) {
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

    @Test
    fun `When findByDateBefore then return BankTransaction List`() {
        val transactions = transactionRepository.findByDateBefore(LocalDate.parse("2001-01-02"))
        assert(transactions.size == 1)
        assert(transactions[0].accountNumber == "01")
        assert(transactions[0].beneficiary == "02")
    }

    @Test
    fun `When findByDateAfter then return BankTransaction List`() {
        val transactions = transactionRepository.findByDateAfter(LocalDate.parse("2001-01-02"))
        assert(transactions.size == 2)
        assert(transactions[0].accountNumber == "02")
        assert(transactions[0].beneficiary == "01")
        assert(transactions[1].accountNumber == "03")
        assert(transactions[1].beneficiary == "02")
    }

    @Test
    fun `When findByDateBetween then return BankTransaction List`() {
        val transactions =
            transactionRepository.findByDateBetween(LocalDate.parse("2001-01-01"), LocalDate.parse("2002-01-01"))
        assert(transactions.size == 2)
        assert(transactions[0].accountNumber == "01")
        assert(transactions[0].beneficiary == "02")
        assert(transactions[1].accountNumber == "02")
        assert(transactions[1].beneficiary == "01")
    }
}