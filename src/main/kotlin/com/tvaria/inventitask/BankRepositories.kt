package com.tvaria.inventitask

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface AccountRepository : JpaRepository<BankAccount, Long> {
    fun findBankAccountByAccountNumber(accountNumber: String): BankAccount?
}

interface TransactionRepository : JpaRepository<BankTransaction, BankAccount> {
    fun findByDateBefore(operationTime: LocalDate): List<BankTransaction>
    fun findByDateAfter(operationTime: LocalDate): List<BankTransaction>
    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate): List<BankTransaction>

    fun findByAccountNumber(accountNumber: String): List<BankTransaction>
    fun findByAccountNumberAndDateBefore(
        accountNumber: String,
        operationTime: LocalDate
    ): List<BankTransaction>

    fun findByAccountNumberAndDateAfter(
        accountNumber: String,
        operationTime: LocalDate
    ): List<BankTransaction>

    fun findByAccountNumberAndDateBetween(
        accountNumber: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<BankTransaction>
}
