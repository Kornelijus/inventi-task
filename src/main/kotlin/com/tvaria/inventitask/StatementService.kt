package com.tvaria.inventitask

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.*
import javax.servlet.http.HttpServletResponse

interface StatementService {
    fun getStatement(from: String?, to: String?): List<BankTransaction>
    fun importStatement(csv: InputStream)
    fun exportStatement(response: HttpServletResponse, from: String?, to: String?)
    fun getAccountBalance(accountNumber: String, from: String?, to: String?): HashMap<Currency, BigDecimal>
}

@Service
class StatementServiceImpl(@Autowired val transactionRepository: TransactionRepository) : StatementService {
    override fun getStatement(from: String?, to: String?): List<BankTransaction> {
        val fromDate: LocalDate?
        val toDate: LocalDate?

        try {
            fromDate = from?.let { LocalDate.parse(it) }
            toDate = to?.let { LocalDate.parse(it) }
        } catch (e: DateTimeParseException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: use yyyy-MM-dd")
        }

        return when {
            fromDate != null && toDate != null -> transactionRepository.findByDateBetween(fromDate, toDate)
            fromDate != null -> transactionRepository.findByDateAfter(fromDate)
            toDate != null -> transactionRepository.findByDateBefore(toDate)
            else -> transactionRepository.findAll()
        }
    }

    override fun importStatement(csv: InputStream) {
        val transactions = mutableListOf<BankTransaction>()
        csvReader().open(csv) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                val amount: BigDecimal
                val currency: Currency
                val date: LocalDate

                try {
                    amount = row["amount"]!!.toBigDecimal()
                    currency = Currency.getInstance(row["currency"]!!)
                    date = LocalDate.parse(row["date"]!!)
                } catch (e: NumberFormatException) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount")
                } catch (e: IllegalArgumentException) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency: use ISO 4217 code")
                } catch (e: DateTimeParseException) {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: use yyyy-MM-dd")
                }

                when {
                    amount.compareTo(BigDecimal.ZERO) == -1 -> throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Amount cannot be negative"
                    )
                    row["account"]!! == row["beneficiary"]!! -> throw ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Account and Beneficiary cannot be the same"
                    )
                    else -> transactions.add(
                        BankTransaction(row["account"]!!, date, row["beneficiary"]!!, row["comment"], amount, currency)
                    )
                }
            }
        }

        transactionRepository.saveAll(transactions)
    }

    override fun exportStatement(response: HttpServletResponse, from: String?, to: String?) {

        response.contentType = "text/csv"
        response.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"")

        val transactions = getStatement(from, to)
        csvWriter().open(response.outputStream) {
            writeRow("account", "date", "beneficiary", "comment", "amount", "currency")
            writeRows(transactions.map {
                listOf(
                    it.accountNumber,
                    it.date.toString(),
                    it.beneficiary,
                    it.comment,
                    it.amount.toString(),
                    it.currency.currencyCode
                )
            })
        }
    }

    override fun getAccountBalance(accountNumber: String, from: String?, to: String?): HashMap<Currency, BigDecimal> {
        val balances = HashMap<Currency, BigDecimal>()
        val transactions = getStatement(from, to)

        when {
            transactions.isEmpty() && (from != null || to != null) -> throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No transactions with account found in provided timeframe"
            )
            transactions.isEmpty() -> throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No transactions with account found"
            )
            else -> transactions.forEach {
                when (accountNumber) {
                    it.accountNumber ->
                        balances[it.currency] = balances.getOrDefault(it.currency, BigDecimal.ZERO) - it.amount
                    it.beneficiary ->
                        balances[it.currency] = balances.getOrDefault(it.currency, BigDecimal.ZERO) + it.amount
                }
            }
        }

        return balances
    }
}
