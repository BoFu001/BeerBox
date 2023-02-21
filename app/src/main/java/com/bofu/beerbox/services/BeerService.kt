package com.bofu.beerbox.services

import android.util.Log
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.NetworkResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface BeerApi {
    @GET("beers")
    suspend fun getBeers(): List<Beer>
}

class BeerService: BaseService() {

    private val TAG = javaClass.simpleName

    private val beerApi: BeerApi by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        retrofit.create(BeerApi::class.java)
    }

    suspend fun getBeers(): NetworkResult {

        return try {
            val response = beerApi.getBeers()
            Log.d(TAG, "getBeers On response, item size: ${response.size}")
            NetworkResult.ResponseSuccess(response)

        } catch (exception: Throwable) {
            Log.d(TAG, "getBeers On failure, message: ${exception.message}")
            NetworkResult.Exception(exception)
        }
    }
}