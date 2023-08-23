package com.example.channels.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.channels.model.retrofit.EpgDb

@Entity(
    tableName = "epgs",
    primaryKeys = [
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
    ]
)
data class EpgDbEntity(
    val channelID: Int,
    val id: Long,
    val timestart: Long,
    val timestop: Long,
    val title: String
) {

    fun toEpgDb(): EpgDb = EpgDb(
        channelID = channelID,
        id = id,
        timestart = timestart,
        timestop = timestop,
        title = title
    )
}
fun EpgDb.fromEpgDb(): EpgDbEntity = EpgDbEntity(
    channelID = this.channelID,
    id = this.id,
    timestart = this.timestart,
    timestop = this.id,
    title = this.title
)
