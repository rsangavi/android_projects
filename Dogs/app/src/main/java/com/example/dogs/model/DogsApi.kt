package com.example.dogs.model

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface DogsApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogsInfo(): Single<List<DogBreed>>
}