package com.dicoding.picodiploma.loginwithanimation.data

import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = apiService.login(email, password)
            if (!response.error) {
                // Jika login berhasil dan token diterima, simpan token ke dalam DataStore
                val user = UserModel(email, response.loginResult.token, true)
                saveSession(user)
            }
            response
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse? {
        return try {
            val response = apiService.register(name, email, password)
            if (!response.error) {
                response
            }else{
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference,apiService)
            }.also { instance = it }
    }
}