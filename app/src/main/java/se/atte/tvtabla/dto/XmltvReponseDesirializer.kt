package se.atte.tvtabla.dto

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode

class XmltvReponseDesirializer : StdDeserializer<ChannelDateInfoDto>(ChannelDateInfoDto::class.java) {

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext?): ChannelDateInfoDto {
        val tree = jsonParser.readValueAsTree<TreeNode>()

        val root = tree.get("jsontv") as ObjectNode
        val programArray = root.get("programme") as ArrayNode

        var programms = mutableListOf<ProgrammeDto?>()
        for (program in programArray) {
            val title = program.get("title")?.get("sv")?.asText() ?: ""
            val desc = program.get("desc")?.get("sv")?.asText() ?: ""
            val start = program.get("start").asLong()
            var stop = program.get("stop").asLong()

            programms.add(ProgrammeDto(title, desc, start, stop))
        }
        var channelName = programArray.get(0).get("channel").asText()

        return ChannelDateInfoDto(channelName = channelName, programmeDtos = programms)
    }

}