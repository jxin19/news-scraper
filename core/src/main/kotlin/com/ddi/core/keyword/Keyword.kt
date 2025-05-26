package com.ddi.core.keyword

import com.ddi.core.common.domain.BaseEntity
import com.ddi.core.common.domain.property.Name
import jakarta.persistence.*

@Entity
@Table(
    name = "keyword",
    indexes = [
        Index(name = "idx_keywords_is_active", columnList = "is_active"),
        Index(name = "idx_keywords_created_at", columnList = "created_at")
    ]
)
class Keyword(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    val id: Long = 0,

    @Embedded
    @AttributeOverride(name = "_value", column = Column(name = "name", nullable = false, length = 100))
    private var name: Name = Name(),

    @Column(name = "is_active", nullable = false)
    private var _isActive: Boolean = true,
) : BaseEntity() {
    val nameValue: String
        get() = this.name.value
        
    val isActive: Boolean
        get() = this._isActive

    fun toggle() {
        this._isActive = !this._isActive
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Keyword

        if (id != other.id) return false
        if (_isActive != other._isActive) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + _isActive.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Keyword(_isActive=$_isActive, name=$name, id=$id)"
    }

}