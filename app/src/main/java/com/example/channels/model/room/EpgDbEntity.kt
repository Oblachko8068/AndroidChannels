package com.example.channels.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.channels.model.retrofit.EpgDb

@Entity(
    tableName = "epgs",
    primaryKeys = [
        "channel_id",
        "id"
    ],
    indices = [
        Index("id")
    ],
    foreignKeys = [
        ForeignKey (
            entity = ChannelDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["channel_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class EpgDbEntity(
    @ColumnInfo(name = "channel_id") val channelID: Int,
    val id: Long,
    @ColumnInfo(name = "time_start") val timestart: Long,
    @ColumnInfo(name = "time_stop") val timestop: Long,
    val title: String
) {

    fun toEpgDb(): EpgDb = EpgDb(
        channelID = channelID,
        id = id,
        timestart = timestart,
        timestop = timestop,
        title = title
    )

    companion object{
        fun fromEpgDb(epgDb: EpgDb): EpgDbEntity = EpgDbEntity(
            channelID = epgDb.channelID,
            id = epgDb.id,
            timestart = epgDb.timestart,
            timestop = epgDb.id,
            title = epgDb.title
        )
    }

}
