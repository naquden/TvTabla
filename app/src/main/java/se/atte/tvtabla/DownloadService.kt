package se.atte.tvtabla

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import se.atte.tvtabla.dto.ChannelDateInfoDto

interface DownloadService {

    @GET("/{file}")
    fun get(@Path("file") id: String): Call<ChannelDateInfoDto>
}