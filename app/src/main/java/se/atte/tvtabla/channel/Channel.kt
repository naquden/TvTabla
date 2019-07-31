package se.atte.tvtabla.channel

import java.util.*

enum class Channel(val id: String, val displayName: String) {
    TV4("hd.tv4.se", "TV4"),
    SVT1("svt1hd.svt.se", "SVT 1");

    fun parseChannelId(cal: Calendar): String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        return String.format("%s_%d-%02d-%d.js.gz", id, year, month, day)
    }

    companion object {
        @JvmStatic
        fun getDisplayName(id: String): String {
            for (channel in values()) {
                if (channel.id == id) {
                    return channel.displayName
                }
            }
            // Default to given ID string
            return id
        }
    }
}