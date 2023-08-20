package com.apex.codeassesment.data.local

import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.model.User.Companion.createRandom
import com.apex.codeassesment.data.remote.RemoteDataSource
import com.apex.codeassesment.di.MainComponent
import com.squareup.moshi.Moshi
import javax.inject.Inject

// TODO (3 points): Convert to Kotlin
// TODO (2 point): Add tests
// TODO (1 point): Use the correct naming conventions.

class LocalDataSource @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    private val moshi = Moshi.Builder().build()

    fun loadUser(): User {
        val serializedUser = preferencesManager.loadUser()
        val jsonAdapter = moshi.adapter(
            User::class.java
        )
        return try {
            // TODO (1 point): Address Android Studio warning
            val user = jsonAdapter.fromJson(serializedUser)
            user ?: createRandom()
        } catch (e: Exception) {
            e.printStackTrace()
            createRandom()
        }
    }

    fun saveUser(user: User) {
        val jsonAdapter = moshi.adapter(
            User::class.java
        )
        val serializedUser = jsonAdapter.toJson(user)
        preferencesManager.saveUser(serializedUser)
    }
}