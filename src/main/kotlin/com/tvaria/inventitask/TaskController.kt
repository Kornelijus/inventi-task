package com.tvaria.inventitask

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
class TaskController(@Autowired val statementService: StatementService) {

    // Specifying multipart/form-data as the content type fixes the issue of Swagger showing @RequestPart only as json,
    // not sure if there's a better way to solve this or if it's just a bug with springdoc-openapi
    @Operation(summary = "Import bank statement for one or several bank accounts via CSV")
    @ApiResponse(responseCode = "200", description = "Statement imported successfully", content = [Content()])
    @ApiResponse(responseCode = "400", description = "Invalid statement format", content = [Content()])
    @PostMapping("/import", consumes = ["multipart/form-data"])
    fun importCSV(@RequestPart csvFile: MultipartFile): ResponseEntity<String> {
        statementService.importStatement(csvFile.inputStream)
        return ResponseEntity.ok("CSV File ${csvFile.originalFilename ?: ""} imported successfully")
    }

    @Operation(summary = "Export bank statement for one or several bank accounts via CSV")
    @ApiResponse(
        responseCode = "200", description = "Statement exported successfully",
        content = [Content(mediaType = "text/csv", schema = Schema())]
    )
    @ApiResponse(responseCode = "400", description = "Invalid date format")
    @GetMapping("/export")
    fun exportCSV(response: HttpServletResponse, from: String?, to: String?) {
        statementService.exportStatement(response, from, to)
    }

    @Operation(summary = "Calculate account balance for given date")
    @ApiResponse(responseCode = "400", description = "Invalid date format", content = [Content()])
    @ApiResponse(responseCode = "404", description = "No transactions found", content = [Content()])
    @GetMapping("/balance")
    fun calculateBalance(accountNumber: String, from: String?, to: String?): HashMap<Currency, BigDecimal> {
        return statementService.getAccountBalance(accountNumber, from, to)
    }
}
