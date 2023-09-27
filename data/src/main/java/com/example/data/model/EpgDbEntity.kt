package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Epg

@Entity(
    tableName = "epgs",
    /*primaryKeys = [
        "channelID",
        "id"
    ],
    indices = [
        Index("id")
    ],
    foreignKeys = [
        ForeignKey (
            entity = ChannelDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["channelID"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]*/
)
data class EpgDbEntity(
    val channelID: Int,
    @PrimaryKey val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
) {

    fun toEpgDb(): Epg {
        return Epg(
            channelID = channelID,
            id = id,
            timestart = timestart,
            timestop = timestop,
            title = title
        )
    }
}

fun ChannelJson.fromChannelJsonToEpgDbEntity(): List<EpgDbEntity> = this.epg.map {
    EpgDbEntity(
        this.id,
        id = it.id,
        timestart = it.timestart,
        timestop = it.timestop,
        title = it.title
    )
}
