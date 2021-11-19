package com.tvaria.inventitask

import org.hibernate.annotations.NaturalId
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

// At this point, the BankAccount Entity is only used for quickly checking if an account exists,
// as storing the balances is pointless when they can be requested with a specific date range,
// which needs to be queried every time.
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
    var amount: BigDecimal,
    var currency: Currency,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)
