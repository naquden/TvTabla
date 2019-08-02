package se.atte.tvtabla.channel

import java.util.*

enum class Channel(val id: String, val displayName: String) {
    SVT1("svt1hd.svt.se", "SVT 1"),
    SVT2("svt2hd.svt.se", "SVT 2"),
    TV3("hd.tv3.se", "TV3"),
    TV4("hd.tv4.se", "TV4"),
    TV5("hd.kanal5.se", "Kanal 5"),
    TV6("hd.tv6.se", "TV6"),
    TV7("sjuan.se", "sjuan"),
    TV8("tv8.se", "8 an"),
    TV9("hd.kanal9.se", "Kanal 9"),
//    TV10("kanal10.se", "Kanal 10"), // NOT WORKING
    TV11("tv11.sbstv.se", "Kanal 11"),
    Tv12("hd.tv12.tv4.se", "Kanal 12");

    fun parseChannelId(cal: Calendar): String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        return String.format("%s_%d-%02d-%02d.js.gz", id, year, month, day)
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