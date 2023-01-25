package io.fournkoner.netschool.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.usecases.account.SignInUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    fun login(
        login: String,
        password: String,
        onResultListener: (Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            onResultListener(signInUseCase(login.trim(), password.trim()).getOrDefault(false))
        }
    }
}