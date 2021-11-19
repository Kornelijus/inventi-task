package com.tvaria.inventitask

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import javax.servlet.http.HttpServletResponse

interface AccountService {
    fun getAccountBalance(
        accountNumber: String,
        fromDate: String?,
        toDate: String?
    ): HashMap<Currency, BigDecimal>
}

interface StatementService {
    fun getStatement(accountNumber: String?, from: String?, to: String?): List<BankTransaction>
    fun importStatement(csvFile: InputStream)
    fun exportStatement(response: HttpServletResponse, from: String?, to: String?)
}

@Service
class AccountServiceImpl(@Autowired val accountRepository: AccountRepository) : AccountService {
    override fun getAccountBalance(
        accountNumber: String,
        fromDate: String?,
        toDate: String?
    ): HashMap<Currency, BigDecimal> {
        TODO("Calculate Account Balance")
    }
}

@Service
class StatementServiceImpl(@Autowired val transactionRepository: TransactionRepository) : StatementService {
    override fun getStatement(accountNumber: String?, from: String?, to: String?): List<BankTransaction> {
        val fromDate = if (from != null) LocalDate.parse(from) else null
        val toDate = if (to != null) LocalDate.parse(to) else null

        accountNumber?.let {
            return when {
                fromDate != null && toDate != null -> transactionRepository.findByAccountNumberAndDateBetween(
                    accountNumber,
                    fromDate,
                    toDate
                )
                fromDate != null -> transactionRepository.findByAccountNumberAndDateBefore(accountNumber, fromDate)
                toDate != null -> transactionRepository.findByAccountNumberAndDateAfter(accountNumber, toDate)
                else -> transactionRepository.findByAccountNumber(accountNumber)
            }
        } ?: run {
            return when {
                fromDate != null && toDate != null -> transactionRepository.findByDateBetween(fromDate, toDate)
                fromDate != null -> transactionRepository.findByDateBefore(fromDate)
                toDate != null -> transactionRepository.findByDateAfter(toDate)
                else -> transactionRepository.findAll()
            }
        }
    }

    override fun importStatement(csv: InputStream) {
        val transactions = mutableListOf<BankTransaction>()
        csvReader().open(csv) {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                transactions.add(
                    BankTransaction(
                        row["account"]!!,
                        LocalDate.parse(row["date"]!!),
                        row["beneficiary"]!!,
                        row["comment"],
                        BigDecimal(row["amount"]!!),
                        Currency.getInstance(row["currency"]!!),
                    )
                )
            }
        }

        transactionRepository.saveAll(transactions)
    }

    override fun exportStatement(response: HttpServletResponse, from: String?, to: String?) {
        //TODO("Export Bank Statement to CSV")
        val transactions = getStatement(null, from, to)
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
}
