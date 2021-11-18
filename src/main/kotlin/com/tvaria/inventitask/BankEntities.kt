package com.tvaria.inventitask

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvDate
import com.opencsv.bean.CsvIgnore
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
    @CsvBindByName @CsvBindByName(column = "account")
    @CsvBindByPosition(position = 0)
    var accountNumber: String,

    @CsvBindByName @CsvBindByName(column = "date") @CsvBindByName(column = "time")
    @CsvBindByPosition(position = 1)
    @CsvDate
    var operationTime: LocalDateTime, // This will be in UTC as the timezone is set in application.properties

    @CsvBindByName
    @CsvBindByPosition(position = 2)
    var beneficiary: String,

    @CsvBindByName @CsvBindByName(column = "reference")
    @CsvBindByPosition(position = 3)
    var comment: String? = null,

    @CsvBindByName
    @CsvBindByPosition(position = 4)
    var amount: BigDecimal, // TODO: make sure the transaction amount is non-negative

    @CsvBindByName
    @CsvBindByPosition(position = 5)
    var currency: Currency,

    @CsvIgnore
    @Id @GeneratedValue var id: Long
)