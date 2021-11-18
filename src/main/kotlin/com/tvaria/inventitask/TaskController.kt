package com.tvaria.inventitask

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class TaskController {

    @PostMapping("/import")
    fun importCSV(csvFile: MultipartFile, timeZone: String = "Ect/UTC") {
        /* TODO: Assuming some banks provide dates in random timezones, not UTC,
            which needs to be handled before writing as UTC to the database. */
        TODO("Import bank statement for one or several bank accounts via CSV")
    }

    @GetMapping("/export")
    fun exportCSV(from: String?, to: String?) {
        TODO("Export bank statement for one or several bank accounts via CSV")
    }

    @GetMapping("/balance")
    fun calculateBalance(accountNumber: String, from: String?, to: String?) {
        TODO("Calculate account balance for given date")
    }
}