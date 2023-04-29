package io.fournkoner.netschool.domain.usecases.account

import io.fournkoner.netschool.domain.repositories.AccountRepository

class LogoutUseCase(private val repository: AccountRepository) {

    suspend operator fun invoke() = repository.logout()
}
