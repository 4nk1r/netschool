package io.fournkoner.netschool.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.usecases.account.GetAccountDataUseCase
import io.fournkoner.netschool.domain.usecases.account.SignInUseCase
import io.fournkoner.netschool.domain.usecases.mail.GetUnreadMessagesCountUseCase
import io.fournkoner.netschool.utils.Const
import io.fournkoner.netschool.utils.debugValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val getAccountDataUseCase: GetAccountDataUseCase,
    private val getUnreadMessagesCountUseCase: GetUnreadMessagesCountUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> get() = _name

    fun login(
        login: String,
        password: String,
        onResultListener: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            val result = signInUseCase(login.trim(), password.trim()).getOrDefault(false)
            if (result) {
                Const.mailUnreadMessages = getUnreadMessagesCountUseCase().debugValue().getOrElse {
                    it.printStackTrace()
                    0
                }
                onResultListener(true)
            } else onResultListener(false)
        }
    }

    fun checkIfFirstLogin(onLoginResultListener: (Boolean) -> Unit): Boolean {
        val account = getAccountDataUseCase()
        if (account != null) {
            _name.value = account.name
            login(account.username, account.password, onLoginResultListener)
        }
        return account == null
    }
}