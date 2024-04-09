package com.leejongsul.tables

object Users : BaseTable() {
    val name = varchar("name", length = 50)
    val username = varchar("username", length = 50)
}