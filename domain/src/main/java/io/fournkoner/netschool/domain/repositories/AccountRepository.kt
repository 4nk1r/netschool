package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.auth.Account

interface AccountRepository {

    suspend fun signIn(login: String, password: String): Result<Boolean>

    suspend fun logout()

    fun getAccountData(): Account?
}