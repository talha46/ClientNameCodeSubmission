package com.apex.codeassesment.data.remote

import com.apex.codeassesment.data.Resource
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.model.UsersResponse
import javax.inject.Inject

// TODO (2 points): Add tests
class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

  // TODO (5 points): Load data from endpoint https://randomuser.me/api
//  fun LoadUser() = User.createRandom()
  suspend fun LoadUser(): Resource<UsersResponse> {
    val result = apiService.getUser()
    if (result.isSuccessful) {
      return Resource.Success(response = result.body())
    }
    return Resource.Failure(error = result.errorBody())
  }
  // TODO (3 points): Load data from endpoint https://randomuser.me/api?results=10
  // TODO (Optional Bonus: 3 points): Handle succes and failure from endpoints
//  fun loadUsers() = (0..10).map { User.createRandom() }
  suspend fun loadUsers(count: Int): Resource<UsersResponse> {
    val result = apiService.getUsers(results = count)
    if (result.isSuccessful) {
      return Resource.Success(response = result.body())
    }
    return Resource.Failure(error = result.errorBody())
  }
}
