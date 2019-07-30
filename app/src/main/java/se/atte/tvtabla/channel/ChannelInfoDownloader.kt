package se.atte.tvtabla.channel

import java.util.*

class ChannelInfoDownloader {

    enum class ChannelId(private val id: String) {
        TV4("hd.tv4.se");

        fun parseChannelId(cal: Calendar) : String {
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val day = cal.get(Calendar.DAY_OF_MONTH)

            return String.format("%s_%d-%02d-%d.js.gz", id, year, month, day)
        }
    }


}