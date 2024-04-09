package com.leejongsul.tables

object Pages : BaseTable() {
    val userId = long("user_id").index()
    val url = varchar("url", length = 256)
    val title = varchar("title", length = 256)
    val scope = varchar("scope", length = 10).index()
}