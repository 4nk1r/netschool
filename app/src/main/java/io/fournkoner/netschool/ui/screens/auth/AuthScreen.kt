package io.fournkoner.netschool.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.fournkoner.netschool.R
import io.fournkoner.netschool.ui.components.VSpace
import io.fournkoner.netschool.ui.navigation.Screen
import io.fournkoner.netschool.ui.style.LocalNetSchoolColors
import io.fournkoner.netschool.ui.style.Shapes
import io.fournkoner.netschool.ui.style.Typography
import io.fournkoner.netschool.utils.autofill
import splitties.toast.UnreliableToastApi
import splitties.toast.toast

@OptIn(ExperimentalComposeUiApi::class, UnreliableToastApi::class)
@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalNetSchoolColors.current.backgroundMain)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val context = LocalContext.current

        var login by rememberSaveable { mutableStateOf("") }
        val loginFocusRequester = remember { FocusRequester() }

        var password by rememberSaveable { mutableStateOf("") }
        val passwordFocusRequester = remember { FocusRequester() }
        var passwordShown by rememberSaveable { mutableStateOf(false) }

        var isLoading by rememberSaveable { mutableStateOf(false) }

        fun login() {
            if (login.isBlank()) {
                loginFocusRequester.requestFocus()
                return
            }
            if (password.isBlank()) {
                passwordFocusRequester.requestFocus()
                return
            }

            keyboardController?.hide()
            isLoading = true
            viewModel.login(login, password) { isSuccess ->
                if (isSuccess) {
                    navController.navigate(Screen.Journal.route) {
                        popUpTo(Screen.Journal.route) { inclusive = true }
                    }
                } else {
                    isLoading = false
                    toast(context.getString(R.string.auth_login_error))
                }
            }
        }

        AppIcon()
        VSpace(24.dp)
        EnterDataCard(
            login = login,
            onLoginChanged = { login = it },
            password = password,
            onPasswordChanged = { password = it },
            loginFocusRequester = loginFocusRequester,
            passwordFocusRequester = passwordFocusRequester,
            passwordShown = passwordShown,
            onTogglePasswordVisibility = { passwordShown = !passwordShown },
            onLogin = ::login
        )
        VSpace(4.dp)
        AuthButton(
            isActive = login.isNotBlank() && password.isNotBlank(),
            isLoading = isLoading,
            onClick = ::login
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AuthButton(
    isActive: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = CornerSize(2.dp),
        topEnd = CornerSize(2.dp),
        bottomStart = Shapes.medium.bottomStart,
        bottomEnd = Shapes.medium.bottomEnd
    )
    val backgroundColor = animateColorAsState(
        if (!isActive || isLoading) {
            LocalNetSchoolColors.current.accentInactive
        } else {
            LocalNetSchoolColors.current.accentMain
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = backgroundColor.value,
                shape = shape
            )
            .clip(shape)
            .clickable(
                role = Role.Button,
                onClickLabel = stringResource(R.string.auth_login_hint),
                enabled = isActive,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                expandHorizontally() + fadeIn() with shrinkHorizontally() + fadeOut()
            }
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = LocalNetSchoolColors.current.onAccent
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_login_action),
                    style = Typography.button.copy(color = LocalNetSchoolColors.current.onAccent),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EnterDataCard(
    login: String,
    onLoginChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    loginFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    passwordShown: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit,
) {
    val shape = RoundedCornerShape(
        topStart = Shapes.medium.topStart,
        topEnd = Shapes.medium.topEnd,
        bottomStart = CornerSize(2.dp),
        bottomEnd = CornerSize(2.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LocalNetSchoolColors.current.backgroundCard,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = LocalNetSchoolColors.current.divider,
                shape = shape
            )
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.auth_title),
            style = Typography.h5.copy(color = LocalNetSchoolColors.current.textMain),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        VSpace(16.dp)
        LoginTextField(
            login = login,
            onLoginChanged = onLoginChanged,
            loginFocusRequester = loginFocusRequester,
            passwordFocusRequester = passwordFocusRequester
        )
        VSpace(8.dp)
        PasswordTextField(
            password = password,
            onPasswordChanged = onPasswordChanged,
            passwordFocusRequester = passwordFocusRequester,
            onLogin = onLogin,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            passwordShown = passwordShown
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun PasswordTextField(
    password: String,
    onPasswordChanged: (String) -> Unit,
    passwordFocusRequester: FocusRequester,
    onLogin: () -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    passwordShown: Boolean,
) {
    AuthTextField(
        value = password,
        onValueChanged = onPasswordChanged,
        focusRequester = passwordFocusRequester,
        placeholder = stringResource(R.string.auth_password_placeholder),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onLogin() }
        ),
        autoFillTypes = listOf(AutofillType.Password),
        leadingIcon = { isFocused ->
            Icon(
                painter = painterResource(R.drawable.ic_key),
                contentDescription = stringResource(R.string.auth_password_placeholder),
                tint = animateColorAsState(
                    if (isFocused) {
                        LocalNetSchoolColors.current.accentMain
                    } else {
                        LocalNetSchoolColors.current.accentInactive
                    }
                ).value
            )
        },
        trailingIcon = { isFocused ->
            IconButton(onClick = onTogglePasswordVisibility) {
                Icon(
                    painter = if (passwordShown) {
                        painterResource(R.drawable.ic_eye_closed)
                    } else {
                        painterResource(R.drawable.ic_eye)
                    },
                    contentDescription = if (passwordShown) {
                        stringResource(R.string.auth_hint_hide_password)
                    } else {
                        stringResource(R.string.auth_hint_show_password)
                    },
                    tint = animateColorAsState(
                        if (isFocused) {
                            LocalNetSchoolColors.current.accentMain
                        } else {
                            LocalNetSchoolColors.current.accentInactive
                        }
                    ).value
                )
            }
        },
        visualTransformation = if (passwordShown) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun LoginTextField(
    login: String,
    onLoginChanged: (String) -> Unit,
    loginFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
) {
    AuthTextField(
        value = login,
        onValueChanged = onLoginChanged,
        focusRequester = loginFocusRequester,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { passwordFocusRequester.requestFocus() }
        ),
        autoFillTypes = listOf(AutofillType.PersonLastName, AutofillType.Username),
        leadingIcon = { isFocused ->
            Icon(
                painter = painterResource(R.drawable.ic_user),
                contentDescription = stringResource(R.string.auth_login_placeholder),
                tint = animateColorAsState(
                    if (isFocused) {
                        LocalNetSchoolColors.current.accentMain
                    } else {
                        LocalNetSchoolColors.current.accentInactive
                    }
                ).value
            )
        },
        placeholder = stringResource(R.string.auth_login_placeholder)
    )
}

@Composable
private fun AppIcon() {
    Image(
        painter = painterResource(R.drawable.ic_netschool),
        contentDescription = stringResource(R.string.app_name),
        modifier = Modifier.size(56.dp)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun AuthTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    focusRequester: FocusRequester,
    placeholder: String,
    leadingIcon: (@Composable (Boolean) -> Unit)? = null,
    trailingIcon: (@Composable (Boolean) -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    autoFillTypes: List<AutofillType> = emptyList(),
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .focusRequester(focusRequester)
            .then(
                if (autoFillTypes.isNotEmpty()) {
                    Modifier.autofill(autoFillTypes, onValueChanged)
                } else Modifier
            ),
        singleLine = true,
        shape = RoundedCornerShape(Shapes.medium.topStart),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = LocalNetSchoolColors.current.textMain,
            cursorColor = LocalNetSchoolColors.current.accentMain,
            focusedBorderColor = LocalNetSchoolColors.current.accentMain,
            focusedLabelColor = LocalNetSchoolColors.current.accentMain,
            unfocusedBorderColor = LocalNetSchoolColors.current.accentInactive,
            unfocusedLabelColor = LocalNetSchoolColors.current.accentInactive,
            placeholderColor = LocalNetSchoolColors.current.textSecondary,
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon?.let { { it(isFocused) } },
        trailingIcon = trailingIcon?.let { { it(isFocused) } },
        label = { Text(placeholder) },
        textStyle = Typography.body1
    )
}