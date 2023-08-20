package com.apex.codeassesment.data.remote

import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.model.UsersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("/api")
    suspend fun getUser(): Response<UsersResponse>

    @GET("/api")
    suspend fun getUsers(
        @Query("results") results: Int
    ): Response<UsersResponse>
}