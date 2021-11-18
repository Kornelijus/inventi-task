package com.tvaria.inventitask

import org.hibernate.annotations.NaturalId
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "Account")
class BankAccount(
    @NaturalId var accountNumber: String,
    @Id @GeneratedValue var id: Long
)

@Entity(name = "Transaction")
class BankTransaction(
    var accountNumber: String,
    // This will be in UTC as the timezone is set in application.properties
    var operationTime: LocalDateTime,
    var beneficiary: String,
    var comment: String? = null,
    // TODO: make sure the transaction amount is non-negative
    var amount: BigDecimal,
    var currency: Currency,
    @Id @GeneratedValue var id: Long
)