package io.fournkoner.netschool.domain.usecases.account

import io.fournkoner.netschool.domain.repositories.AccountRepository

class SignInUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke(login: String, password: String) =
        repository.signIn(login, password)
}