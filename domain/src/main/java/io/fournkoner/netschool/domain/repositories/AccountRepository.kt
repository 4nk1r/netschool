package io.fournkoner.netschool.domain.repositories

interface AccountRepository {

    suspend fun signIn(login: String, password: String): Result<Boolean>

    suspend fun logout()
}