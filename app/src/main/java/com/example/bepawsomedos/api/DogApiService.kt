package com.example.bepawsomedos.api

import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path
interface DogApiService {
    @GET("breeds/list/all")
    fun getBreeds(): Call<DogBreedsResponse>
    @GET("breed/{breed}/images/random/3")
    fun getDogImages(@Path("breed") breed: String?): Call<DogApiResponse2>
    @GET("breed/{breed}/images/random")
    fun getDogImage(@Path("breed") breed: String?): Call<DogApiResponse>


}