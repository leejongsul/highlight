package com.leejongsul.tables

object Mentions : BaseTable() {
    val pageId = long("page_id").index()
    val mentionUserId = long("mention_user_id").index()
}