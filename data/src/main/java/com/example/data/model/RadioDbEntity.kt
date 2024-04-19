package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Channel
import com.example.domain.model.Radio

@Entity(
    tableName = "radios"
)

data class RadioDbEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val stream: String
) {
    fun toRadioDb(): Radio {
        return Radio(
            id = id,
            name = name,
            image = image,
            stream = stream
        )
    }
}

fun Radio.fromRadioToRadioDbEntity(): RadioDbEntity = RadioDbEntity(
    id = this.id,
    name = this.name,
    image = this.image,
    stream = this.stream
)