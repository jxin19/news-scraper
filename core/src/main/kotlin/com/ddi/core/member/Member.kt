package com.ddi.core.member

import com.ddi.core.common.domain.BaseEntity
import com.ddi.core.member.property.Password
import com.ddi.core.member.property.Username
import jakarta.persistence.*

@Entity
@Table(
    name = "member",
    indexes = [
        Index(name = "idx_member_username", columnList = "username", unique = true),
    ]
)
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", length = 10, nullable = false, updatable = false, unique = true)
    val id: Long = 0,

    @Embedded
    private var username: Username? = null,

    @Embedded
    private var password: Password? = null,
) : BaseEntity() {

    val usernameValue: String
        get() = this.username?.value ?: ""

    val passwordValue: String
        get() = this.password?._value ?: ""

    fun update(member: Member) {
        this.username = member.username

        if (this.password != member.password) {
            this.password = member.password
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member

        if (id != other.id) return false
        if (username != other.username) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Member(password=$password, username=$username, id=$id)"
    }

}