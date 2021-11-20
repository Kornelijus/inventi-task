package com.tvaria.inventitask

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface TransactionRepository : JpaRepository<BankTransaction, Long> {
    fun findByDateBefore(operationTime: LocalDate): List<BankTransaction>
    fun findByDateAfter(operationTime: LocalDate): List<BankTransaction>
    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate): List<BankTransaction>
}
