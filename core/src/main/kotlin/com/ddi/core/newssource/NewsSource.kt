package com.ddi.core.newssource

import com.ddi.core.common.domain.BaseEntity
import com.ddi.core.newssource.property.NewsSourceCode
import com.ddi.core.newssource.property.NewsSourceName
import com.ddi.core.newssource.property.NewsSourceUrl
import com.ddi.core.newssource.property.NewsSourceType
import jakarta.persistence.*

@Entity
@Table(
    name = "news_source",
    indexes = [
        Index(name = "idx_news_source_is_active", columnList = "is_active"),
        Index(name = "idx_news_source_url", columnList = "url"),
        Index(name = "idx_news_source_type", columnList = "type")
    ]
)
class NewsSource(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    val id: Long = 0,

    @Embedded
    val name: NewsSourceName = NewsSourceName(),

    @Embedded
    val url: NewsSourceUrl = NewsSourceUrl(),

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, length = 16)
    val code: NewsSourceCode = NewsSourceCode.GOOGLE,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    val type: NewsSourceType = NewsSourceType.RSS_STATIC,

    @Column(name = "is_active", nullable = false)
    private var _isActive: Boolean = true
) : BaseEntity() {
    val nameValue: String
        get() = this.name.value

    val urlValue: String
        get() = this.url.value

    val isActive: Boolean
        get() = _isActive

    fun activate() {
        this._isActive = true
    }

    fun deactivate() {
        this._isActive = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsSource

        if (id != other.id) return false
        if (_isActive != other._isActive) return false
        if (name != other.name) return false
        if (url != other.url) return false
        if (code != other.code) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + _isActive.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "NewsSource(_isActive=$_isActive, type=$type, code=$code, url=$url, name=$name, id=$id)"
    }

}