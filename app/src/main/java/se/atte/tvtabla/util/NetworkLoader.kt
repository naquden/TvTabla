package se.atte.tvtabla.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import se.atte.tvtabla.dto.ChannelDateInfoDto

class ChannelNetworkLoader {

    private val calls = mutableListOf<Call<ChannelDateInfoDto>>()
    private val results = mutableListOf<ChannelDateInfoDto>()

    fun addCall(call: Call<ChannelDateInfoDto>) {
        calls.add(call)
    }

    fun load(): LiveData<Resource<List<ChannelDateInfoDto>>> {
        val result = MutableLiveData<Resource<List<ChannelDateInfoDto>>>()
        val expectedRunCalls = calls.size
        var currentRunCalls = 0

        for (call in calls) {
            call.enqueue(object : Callback<ChannelDateInfoDto> {

                override fun onResponse(call: Call<ChannelDateInfoDto>, response: Response<ChannelDateInfoDto>) {
                    if (response.isSuccessful && response.body() != null) {
                        results.add(response.body() as ChannelDateInfoDto)
                    } else {
                        result.postValue(Resource.error("request failed", results))
                    }

                    if (++currentRunCalls == expectedRunCalls) {
                        result.postValue(Resource.success(results))
                    } else {
                        result.postValue(Resource.loading("", results))
                    }
                }

                override fun onFailure(call: Call<ChannelDateInfoDto>, t: Throwable) {
                    result.postValue(Resource.error(Exception(t), call.toString(), null))
                }

            })
        }

        return result
    }

}