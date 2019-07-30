package se.atte.tvtabla.channel

import se.atte.tvtabla.dto.ChannelDateInfoDto

class ChannelDateInfo {

    constructor(channelDateInfoDto: ChannelDateInfoDto) {
        val channelName = channelDateInfoDto.channelName
        val programmes = mutableListOf<Programme>()
        for (programmDto in channelDateInfoDto.programmeDtos) {
            programmes.add(Programme(programmDto?.title, programmDto?.channel, programmDto?.start, programmDto?.stop))
        }
    }

    data class Programme(
        var title: String?,
        var channel: String?,
        var start: Long?,
        var stop: Long?
    )
}