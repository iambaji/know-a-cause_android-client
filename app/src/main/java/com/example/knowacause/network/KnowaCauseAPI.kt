package com.example.knowacause.network

import retrofit2.Call
import retrofit2.http.*

interface  KnowaCauseAPI{

    @GET("logout")
    fun logout(@Header("Authorization") token : String): Call<Object>

    @POST("register")
    fun register(@Body newUser : User) : Call<Object>

    @GET("user/{email}")
    fun getUserDetails(@Path("email") email : String) : Call<UserFromDB>

    @POST("login")
    fun login( @Body loginuser: LoginUser) : Call<Token>

    @GET("usersposts")
    fun getUsersPosts() : Call<List<PostFromServer>>

    @GET("usersposts/{email}")
    fun getOwnposts(@Path("email") email : String) : Call<List<PostFromServer>>

    @GET("loginstatus")
    fun getLoginStatus(@Header("Authorization") token: String) : Call<Status>

    @POST("usersposts")
    fun savePost(@Header("Authorization") token: String, @Body newpost : PostFromClient) : Call<PostFromServer>

    @POST("notifications")
    fun saveNotifications(@Header("Authorization") sessionId: String, @Body newNotification: NotificationFromClient) : Call<NotificationFromServer>
    // never put a slash in front of your endpoint path when using retrofit, spent 4 hours trying to solve what went wrong, it was single slash...
    @GET("notifications/{email}")
    fun getNotifications( @Header("Authorization") token: String ,@Path("email")  email : String ) : Call<List<NotificationFromServer>>

}