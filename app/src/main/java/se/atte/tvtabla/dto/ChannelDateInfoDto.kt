package se.atte.tvtabla.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize(using = XmltvReponseDesirializer::class)
data class ChannelDateInfoDto(
    var channelName: String?,
    var programmeDtos: MutableList<ProgrammeDto?>
)

data class ProgrammeDto(
    var title: String?,
    var channel: String?,
    var start: Long?,
    var stop: Long?
)