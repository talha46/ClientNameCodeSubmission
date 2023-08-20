package com.apex.codeassesment.data

import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.model.UsersResponse
import com.apex.codeassesment.data.remote.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

// TODO (2 points) : Add tests
// TODO (3 points) : Hide this class through an interface, inject the interface in the clients instead and remove warnings
interface UserRepository {
    suspend fun getUserFromRemote(): Resource<UsersResponse>
    suspend fun getUsersFromRemote(count: Int): Resource<UsersResponse>
    fun getSavedUser(): User
    suspend fun getUser(forceUpdate: Boolean): User
}

class UserRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : UserRepository {

    private val savedUser = AtomicReference(User())

    override fun getSavedUser() = localDataSource.loadUser()

    override suspend fun getUser(forceUpdate: Boolean): User {
        if (forceUpdate) {
            CoroutineScope(Dispatchers.IO).async {
                val result = getUserFromRemote()
                when (result) {
                    is Resource.Success -> {
                        result.response?.results?.let { user ->
                            if (user.isNotEmpty()) {
                                localDataSource.saveUser(user[0])
                                savedUser.set(user[0])
                            }
                        }
                    }

                    is Resource.Failure -> {
                        savedUser.set(User(error = result.error.toString()))
                    }

                    else -> {}
                }
            }.await()
        } else {
            savedUser.set(localDataSource.loadUser())
        }
        return savedUser.get()
    }

    //  fun getUsers() = remoteDataSource.loadUsers()
    override suspend fun getUserFromRemote() = remoteDataSource.LoadUser()

    override suspend fun getUsersFromRemote(count: Int) = remoteDataSource.loadUsers(count = count)
}
