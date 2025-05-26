package com.ddi.core.newsitem

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.time.Instant

@Entity
@Immutable
@Table(
    name = "news_item_mv",
    indexes = [
        Index(name = "idx_news_item_mv_url", columnList = "url"),
        Index(name = "idx_news_item_mv_keyword_id", columnList = "keyword_id"),
        Index(name = "idx_news_item_mv_source_id", columnList = "source_id"),
        Index(name = "idx_news_item_mv_published_date", columnList = "published_date"),
        Index(name = "idx_news_item_mv_keyword_collected", columnList = "keyword_id, collected_at"),
        Index(name = "idx_news_item_mv_source_collected", columnList = "source_id, collected_at"),
        Index(name = "idx_news_item_mv_keyword_name", columnList = "keyword_name"),
        Index(name = "idx_news_item_mv_source_name", columnList = "source_name")
    ]
)
class NewsItemView(
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    val id: Long = 0,

    @Column(name = "keyword_id", nullable = false)
    val keywordId: Long = 0,

    @Column(name = "keyword_name", nullable = false, length = 100)
    val keywordName: String = "",

    @Column(name = "source_id", nullable = false)
    val sourceId: Long = 0,

    @Column(name = "source_name", nullable = false, length = 32)
    val sourceName: String = "",

    @Column(name = "title", nullable = false, length = 255)
    val title: String = "",

    @Column(name = "url", nullable = false, length = 1000)
    val url: String = "",

    @Column(name = "content")
    val content: String? = null,

    @Column(name = "published_date")
    val publishedDate: Instant? = null,

    @Column(name = "collected_at", nullable = false, updatable = false)
    val collectedAt: Instant = Instant.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsItemView

        if (id != other.id) return false
        if (keywordId != other.keywordId) return false
        if (sourceId != other.sourceId) return false
        if (keywordName != other.keywordName) return false
        if (sourceName != other.sourceName) return false
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
        result = 31 * result + keywordName.hashCode()
        result = 31 * result + sourceName.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + (publishedDate?.hashCode() ?: 0)
        result = 31 * result + collectedAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "NewsItemView(id=$id, keywordId=$keywordId, keywordName='$keywordName', sourceId=$sourceId, sourceName='$sourceName', title='$title', url='$url', content=$content, publishedDate=$publishedDate, collectedAt=$collectedAt)"
    }
}