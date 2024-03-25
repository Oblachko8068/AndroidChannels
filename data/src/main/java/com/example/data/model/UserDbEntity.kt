package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.User

@Entity(
    tableName = "users",
)

data class UserDbEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val phone: String,
    val email: String,
    val google: Boolean,
    val image: Int,
    val subscription: Boolean,
) {
    fun toUserDb(): User {
        return User(
            id = id,
            displayName = displayName,
            phone = phone,
            email = email,
            image = image,
            google = google,
            subscription = subscription,
        )
    }
}

fun User.toDbEntity(): UserDbEntity = UserDbEntity(
    id = this.id,
    displayName = this.displayName,
    phone = this.phone,
    email = this.email,
    image = this.image,
    google = this.google,
    subscription = this.subscription,
)