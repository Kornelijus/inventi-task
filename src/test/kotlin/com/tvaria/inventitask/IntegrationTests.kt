package com.tvaria.inventitask

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun `Assert that Swagger UI is accessible`() {
        println(">> Assert that Swagger UI is accessible")
        val entity = restTemplate.getForEntity<String>("/v1/swagger-ui.html")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("OpenAPI definition", "task-controller")
    }

    @Test
    fun `Assert that OpenApi Schema is accessible`() {
        println(">> Assert that OpenApi Schema is accessible")
        val entity = restTemplate.getForEntity<String>("/v1/openapi.json")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("\"info\":{\"title\":\"OpenAPI definition\",\"version\":\"v0\"}")
    }

//    @Test
//    fun `Assert that Account balances calculated correctly`() {
//        val entity = restTemplate.getForEntity<String>("/v1/balance?accountNumber=01")
//        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(entity.body).contains("\"EUR\": 0.5")
//    }
}