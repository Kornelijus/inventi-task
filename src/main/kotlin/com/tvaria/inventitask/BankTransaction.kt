package com.tvaria.inventitask

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class BankTransaction(
    var accountNumber: String,
    var date: LocalDate, // This will be in UTC as the timezone is set in application.properties
    var beneficiary: String,
    var comment: String? = null,
    var amount: BigDecimal,
    var currency: Currency,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)
