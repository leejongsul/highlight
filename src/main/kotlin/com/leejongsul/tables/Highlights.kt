package com.leejongsul.tables

data class Highlight(
    val pageId: Long,
    val color: String,
    val text: String
)

object Highlights : BaseTable() {
    val pageId = long("page_id").index()
    val color = varchar("color", length = 10)
    val text = text("text")
}