package se.atte.tvtabla.channel

import se.atte.tvtabla.dto.ChannelDateInfoDto

class ChannelDateInfo {

    val channelId: String?
    val programmes : MutableList<Programme>

    constructor(channelDateInfoDto: ChannelDateInfoDto) {
        channelId = channelDateInfoDto.channelName
        programmes = mutableListOf()
        for (programmDto in channelDateInfoDto.programmeDtos) {
            programmes.add(Programme(programmDto?.title, programmDto?.channel, programmDto?.start!!, programmDto?.stop!!))
        }
    }

    fun getDisplayName(): String {
        var displayName = ""
        if (channelId != null) {
            displayName = Channel.getDisplayName(channelId)
        }
        return displayName
    }

    data class Programme(
        var title: String?,
        var channel: String?,
        var start: Long,
        var stop: Long
    )
}