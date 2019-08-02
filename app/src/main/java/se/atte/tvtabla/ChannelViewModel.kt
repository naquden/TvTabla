package se.atte.tvtabla

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import se.atte.tvtabla.channel.Channel
import se.atte.tvtabla.dto.ChannelDateInfoDto
import se.atte.tvtabla.util.ChannelNetworkLoader
import se.atte.tvtabla.util.Resource
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

    fun loadChannelInfo(): LiveData<Resource<List<ChannelDateInfoDto>>> {
        val result = MediatorLiveData<Resource<List<ChannelDateInfoDto>>>()
        val loader = ChannelNetworkLoader()


        val cal = Calendar.getInstance()
        val channels = getFavChannels()

        // Add all network calls to list
        for (channel in channels) {
            Log.d("atte2", "download channel info for: " + channel.id)
            val call = downloadService.get(channel.parseChannelId(cal))
            loader.addCall(call)
        }

        // Fetch all calls
        val channelNetworkLoaderResult = loader.load()
        result.addSource(channelNetworkLoaderResult) { loaderResource ->
            when (loaderResource?.status) {
                Resource.Status.LOADING -> {
                    Log.d("atte3", "network result LOADING")
                }
                Resource.Status.ERROR -> {
                    Log.d("atte3", "network result ERROR")
                }
                Resource.Status.SUCCESS -> {
                    Log.d("atte3", "network result SUCCESS")
                    result.postValue(Resource.success(loaderResource.data!!))
                }
            }
        }


        return result
    }

    // TODO: have user selected channels
    fun getFavChannels(): List<Channel> {
        return listOf(*Channel.values())
    }
}