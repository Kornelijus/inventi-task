package com.tvaria.inventitask

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
class TaskController(@Autowired val accountService: AccountService, @Autowired val statementService: StatementService) {

    // Specifying multipart/form-data as the content type fixes the issue of Swagger showing @RequestPart only as json,
    // not sure if there's a better way to solve this or if it's just a bug with springdoc-openapi
    @PostMapping("/import", consumes = ["multipart/form-data"])
    fun importCSV(@RequestPart csvFile: MultipartFile) {
        statementService.importStatement(csvFile.inputStream)
        // TODO: return a meaningful response
    }

    @GetMapping("/export", produces = ["text/csv"])
    fun exportCSV(response: HttpServletResponse, from: String?, to: String?) {
        statementService.exportStatement(response, from, to)
    }

    @GetMapping("/balance")
    fun calculateBalance(accountNumber: String, from: String?, to: String?): HashMap<Currency, BigDecimal> {
        return accountService.getAccountBalance(accountNumber, from, to)
    }
}