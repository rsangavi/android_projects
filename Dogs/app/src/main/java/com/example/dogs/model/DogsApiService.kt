package com.example.dogs.model

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class DogsApiService {
    private val baseUrl = "https://raw.githubusercontent.com/"

    private val api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(DogsApi::class.java)

    fun getDogInfo() = api.getDogsInfo()
}