package com.tvaria.inventitask

import org.hibernate.annotations.NaturalId
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

class Money(val amount: String, val currency: String)

@Entity
class BankAccount(
    @NaturalId var accountNumber: String,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)

@Entity
class BankTransaction(
    var accountNumber: String,
    var date: LocalDate, // This will be in UTC as the timezone is set in application.properties
    var beneficiary: String,
    var comment: String? = null,
    var amount: BigDecimal, // TODO: make sure the transaction amount is non-negative
    var currency: Currency,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)