package com.leejongsul.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

open class BaseTable : Table() {
    val id = long("id").autoIncrement()
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}