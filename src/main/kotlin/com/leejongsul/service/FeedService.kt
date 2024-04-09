package com.leejongsul.service

import com.leejongsul.routes.feeds.Feed
import com.leejongsul.routes.feeds.Highlight
import com.leejongsul.tables.Highlights
import com.leejongsul.tables.Mentions
import com.leejongsul.tables.Pages
import com.leejongsul.tables.Users
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.or

class FeedService : BaseService() {
    suspend fun getFeeds(userId: Long, lastPageId: Long? = null): List<Feed> {
        val feeds = dbQuery {
            val user = Users.select(Users.id).where { Users.id.eq(userId) }.singleOrNull()
            if (user == null) {
                throw NotFoundException()
            }

            val query = Pages.join(Users, JoinType.INNER, onColumn = Users.id, otherColumn = Pages.userId)
                .select(
                    Users.name,
                    Users.username,
                    Pages.id,
                    Pages.url,
                    Pages.title,
                    Pages.createdAt
                )
                .orderBy(Pages.id, SortOrder.DESC)
                .limit(3)

            query.where {
                Pages.userId.eq(userId) or
                        Pages.scope.eq("public") or
                        Pages.id.inSubQuery(Mentions.select(Mentions.pageId)
                            .where { Mentions.mentionUserId.eq(userId) })
            }
            lastPageId?.let {
                query.andWhere { Pages.id less it }
            }
            query.map {
                Feed(
                    name = it[Users.name],
                    username = it[Users.username],
                    createdAt = it[Pages.createdAt],
                    pageId = it[Pages.id],
                    url = it[Pages.url],
                    title = it[Pages.title],
                    highlights = Highlights
                        .select(Highlights.color, Highlights.text)
                        .where { Highlights.pageId.eq(it[Pages.id]) }
                        .limit(3)
                        .map { row ->
                            Highlight(row[Highlights.text], row[Highlights.color])
                        }
                )
            }
        }

        return feeds
    }
}