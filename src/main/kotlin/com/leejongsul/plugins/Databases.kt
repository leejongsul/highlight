package com.leejongsul.plugins

import com.leejongsul.PublicScope
import com.leejongsul.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val databaseConfig = environment.config.config("database")
    val dataSource = HikariDataSource(HikariConfig().apply {
        driverClassName = databaseConfig.property("driverClassName").getString()
        jdbcUrl = databaseConfig.property("jdbcUrl").getString()
        minimumIdle = databaseConfig.property("minimumIdle").getString().toInt()
        maximumPoolSize = databaseConfig.property("maximumPoolSize").getString().toInt()
        isAutoCommit = databaseConfig.property("isAutoCommit").toString().toBoolean()
        transactionIsolation = databaseConfig.property("transactionIsolation").getString()
        username = databaseConfig.property("username").getString()
        password = databaseConfig.property("password").getString()
        validate()
    })
    Database.connect(dataSource)

    if (databaseConfig.property("isInit").toString().toBoolean()) {
        val tables = arrayOf(Users, Pages, Highlights, Mentions)

        transaction {
            SchemaUtils.drop(*tables)
            SchemaUtils.create(*tables)

            for (i in 1..100) {
                Users.insert {
                    it[name] = "테스트$i"
                    it[username] = "testUser$i"
                    it[createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
            }

            (1..1_000_000).chunked(1000).forEach { chucked ->
                Pages.batchInsert(chucked) {
                    val id = (1L..100L).shuffled().last()
                    this[Pages.userId] = id
                    this[Pages.url] = "https://test.com/$it"
                    this[Pages.title] = "타이틀 $it"
                    this[Pages.scope] = PublicScope.entries[(0..2).shuffled().last()].name.lowercase()
                    this[Pages.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
            }

            Pages.select(Pages.id).flatMap { page ->
                (1..(1..10).shuffled().last()).map {
                    Highlight(page[Pages.id], "color$it", "text$it")
                }
            }.chunked(1000).forEach { chucked ->
                Highlights.batchInsert(chucked) {
                    this[Highlights.pageId] = it.pageId
                    this[Highlights.color] = it.color
                    this[Highlights.text] = it.text
                    this[Highlights.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                }
            }
            Pages.select(Pages.id, Pages.userId, Pages.scope).where { Pages.scope.eq("mentioned") }.chunked(1000)
                .forEach { chunked ->
                    Mentions.batchInsert(chunked) {
                        this[Mentions.pageId] = it[Pages.id]
                        this[Mentions.mentionUserId] = (1..100L).shuffled().last { last -> last != it[Pages.userId] }
                        this[Mentions.createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    }
                }
        }
    }
}
