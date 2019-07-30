/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package se.atte.tvtabla

import android.app.Activity
import android.os.Bundle
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import se.atte.tvtabla.channel.ChannelDateInfo
import se.atte.tvtabla.channel.ChannelInfoDownloader
import se.atte.tvtabla.dto.ChannelDateInfoDto
import java.util.*

/**
 * Loads [MainFragment].
 */
class MainActivity : Activity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cal = Calendar.getInstance()
        val channels = getFavChannels()
        for (channel in channels) {
            val call = downloadService.get(channel.parseChannelId(cal))
            call.enqueue(object : Callback<ChannelDateInfoDto> {
                override fun onResponse(call: Call<ChannelDateInfoDto>, dateInfoDto: Response<ChannelDateInfoDto>) {
                    Log.d("atte2", "onResponse")
                    val channelDateInfoDto = dateInfoDto.body()
                    if (dateInfoDto.isSuccessful && channelDateInfoDto != null) {
                        Log.d("atte2", "response.isSuccessful")
                        val channelDateInfo = ChannelDateInfo(channelDateInfoDto)
                        loadUiWithChannelDateInfo(channelDateInfo)
                    }
                }

                override fun onFailure(call: Call<ChannelDateInfoDto>, t: Throwable) {
                    Log.d("atte2", "onFailure")
                }
            })
        }
    }

    fun getFavChannels(): List<ChannelInfoDownloader.ChannelId> {
        return listOf(ChannelInfoDownloader.ChannelId.TV4)
    }

    fun loadUiWithChannelDateInfo(vararg channelInfoList: ChannelDateInfo) {

    }
}