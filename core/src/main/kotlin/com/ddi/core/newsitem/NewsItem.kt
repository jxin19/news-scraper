package com.ddi.core.newsitem

import com.ddi.core.newsitem.property.NewsTitle
import com.ddi.core.newsitem.property.NewsUrl
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "news_item",
    indexes = [
        Index(name = "idx_news_item_url", columnList = "url", unique = true),
        Index(name = "idx_news_items_keyword_id", columnList = "keyword_id"),
        Index(name = "idx_news_items_source_id", columnList = "source_id"),
        Index(name = "idx_news_items_published_date", columnList = "published_date"),
        Index(name = "idx_news_items_keyword_collected", columnList = "keyword_id, collected_at"),
        Index(name = "idx_news_items_source_collected", columnList = "source_id, collected_at")
    ]
)
class NewsItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    val id: Long = 0,

    @Column(name = "keyword_id", nullable = false)
    val keywordId: Long,

    @Column(name = "source_id", nullable = false)
    val sourceId: Long,

    @Embedded
    val title: NewsTitle,

    @Embedded
    val url: NewsUrl,

    @Column(name = "content")
    val content: String? = null,

    @Column(name = "published_date")
    private var publishedDate: Instant? = null,

    @Column(name = "collected_at", nullable = false, updatable = false)
    val collectedAt: Instant = Instant.now()
) {

    val titleValue: String
        get() = this.title.value

    val urlValue: String
        get() = this.url.value

    fun getPublishedDate(): Instant? = publishedDate

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsItem

        if (id != other.id) return false
        if (keywordId != other.keywordId) return false
        if (sourceId != other.sourceId) return false
        if (title != other.title) return false
        if (url != other.url) return false
        if (content != other.content) return false
        if (publishedDate != other.publishedDate) return false
        if (collectedAt != other.collectedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + keywordId.hashCode()
        result = 31 * result + sourceId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (publishedDate?.hashCode() ?: 0)
        result = 31 * result + collectedAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "NewsItem(id=$id, keywordId=$keywordId, sourceId=$sourceId, title=$title, url=$url, content=$content, publishedDate=$publishedDate, collectedAt=$collectedAt)"
    }
}