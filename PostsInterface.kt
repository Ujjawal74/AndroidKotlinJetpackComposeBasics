package com.example.networkingapp

import retrofit2.http.GET

interface PostsInterface {
    @GET("posts")
    suspend fun getPosts() : Posts
}