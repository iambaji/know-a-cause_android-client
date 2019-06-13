package com.example.knowacause.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class NetworkModule{


   companion object {
       private var retrofit : Retrofit? = null
       val baseUrl = "https://knowacause.herokuapp.com/knowacause/api/";
       //val baseUrl =" https://76de51fe.ngrok.io/knowacause/api/"
       fun getRetrofit(): Retrofit?{
           if(retrofit !=null)
           {
               return retrofit
           }
           else
           {
               return Retrofit.Builder().baseUrl(baseUrl)
                       .addConverterFactory(MoshiConverterFactory.create())
                       .build()
           }
       }
   }
}