package io.fournkoner.netschool.domain.usecases.account

import io.fournkoner.netschool.domain.repositories.AccountRepository

class GetAccountDataUseCase(private val accountRepository: AccountRepository) {

    operator fun invoke() = accountRepository.getAccountData()
}
