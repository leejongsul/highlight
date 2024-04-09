package com.leejongsul

import com.leejongsul.routes.feeds.Feed
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        client.get("/feeds/100000").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
        val response = client.get("/feeds/1")
        val list: List<Feed> = Json.decodeFromString(response.bodyAsText())
        println("$list")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(list.size, 3)

        client.get("/feeds/1?lastPageId=${list.last().pageId}").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
