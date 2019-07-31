package se.atte.tvtabla

import android.util.Log
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import se.atte.tvtabla.channel.Channel
import se.atte.tvtabla.channel.ChannelDateInfo
import se.atte.tvtabla.dto.ChannelDateInfoDto
import java.util.*

class ChannelViewModel : ViewModel() {

    val BASE_URL = "http://json.xmltv.se"

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()
        )
        .build()

    val downloadService = retrofit.create(DownloadService::class.java)


    fun loadChannelInfo() {
        val cal = Calendar.getInstance()
        val channels = getFavChannels()
        for (channel in channels) {
            Log.d("atte2", "download channel info for: " + channel.id)
            val call = downloadService.get(channel.parseChannelId(cal))
            call.enqueue(object : Callback<ChannelDateInfoDto> {
                override fun onResponse(call: Call<ChannelDateInfoDto>, dateInfoDto: Response<ChannelDateInfoDto>) {
                    Log.d("atte2", "onResponse")
                    val channelDateInfoDto = dateInfoDto.body()
                    if (dateInfoDto.isSuccessful && channelDateInfoDto != null) {
                        Log.d("atte2", "response.isSuccessful")
                        val channelDateInfo = ChannelDateInfo(channelDateInfoDto)
                        // TODO: return livedata OK
                        //  loadUiWithChannelDateInfo(channelDateInfo)
                    }
                }

                override fun onFailure(call: Call<ChannelDateInfoDto>, t: Throwable) {
                    Log.d("atte2", "onFailure: ", t)
                }
            })
        }
    }

    // TODO: have user selected channels
    fun getFavChannels(): List<Channel> {
        return listOf(*Channel.values())
    }
}