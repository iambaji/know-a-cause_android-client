package com.example.knowacause.network

data class User(val username: String, val email : String, val ngoname : String, val ngoabout: String, val password_hash: String, val ngophone: String)

data class UserFromDB(val id: String, val username: String, val email : String, val ngoname : String, val ngoabout: String, val password_hash: String, val ngophone: String)

data class LoginUser(val email: String, val password_hash: String)


data class PostFromServer(val post_title: String, val post_description : String, val added_by : String, val post_id : String, val date : String)
data class PostFromClient(val post_title: String, val post_description : String, val added_by : String)

data class NotificationFromClient(val notification_post_id :String,
                                  val notification_user : String,
                                  val notification_guest_contact : String,
                                  val notification_guest_name : String )

data class NotificationFromServer(val notification_post_id :String,
                                  val notification_user : String,
                                  val notification_date : String,
                                  val notification_id : String,
                                  val notification_guest_contact : String,
                                  val notification_guest_name : String )

data class Status (val status : Boolean)

data class Token(val access_token : String,val refresh_token : String)