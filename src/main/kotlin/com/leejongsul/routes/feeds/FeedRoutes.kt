package com.leejongsul.routes.feeds

import com.leejongsul.service.FeedService
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Highlight(val text: String, val color: String)

@Serializable
data class Feed(
    val name: String,
    val username: String,
    val createdAt: LocalDateTime,
    val pageId: Long,
    val url: String,
    val title: String,
    val highlights: List<Highlight> = listOf()
)

@Resource("/feeds")
class Feeds {
    @Resource("{id}")
    class Id(val parent: Feeds = Feeds(), val id: Long, val lastPageId: Long? = null)
}

fun Route.getFeeds(feedService: FeedService) {
    get<Feeds.Id> {
        call.respond(
            status = HttpStatusCode.OK,
            message = feedService.getFeeds(it.id, it.lastPageId)
        )
    }
}