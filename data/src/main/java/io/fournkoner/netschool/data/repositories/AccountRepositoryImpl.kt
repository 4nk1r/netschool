package io.fournkoner.netschool.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import io.fournkoner.netschool.data.network.AuthService
import io.fournkoner.netschool.data.utils.*
import io.fournkoner.netschool.domain.entities.auth.Account
import io.fournkoner.netschool.domain.repositories.AccountRepository

internal class AccountRepositoryImpl(
    private val authService: AuthService,
    private val encryptedPreferences: SharedPreferences,
) : AccountRepository {

    override suspend fun signIn(login: String, password: String): Result<Boolean> {
        return runCatching {
            val school = authService.findSchool()
                .find { it.name == Const.SCHOOL_NAME }
                ?: throw IllegalArgumentException(
                    "${Const.SCHOOL_NAME} wasn't found on ${Const.HOST}. " +
                            "Check your private_const.properties file"
                )
            Const.fullSchoolName = school.fullName
            val authData = authService.getAuthData()
            Const.ver = authData.ver
            val passwordHash = hexMD5(authData.salt + hexMD5(password, "windows-1251"))

            val signInRequestBody = mapOf(
                "LoginType" to 1,
                "scid" to school.id,
                "UN" to login,
                "lt" to authData.lt,
                "pw2" to passwordHash,
                "ver" to authData.ver
            ).toFormDataBodyString().debugValue()

            val authResponse = authService.signIn(signInRequestBody).debugValue()
            Const.at = authResponse.at

            val diaryInitResponse = authService.initDiary().debugValue()
            Const.studentId = diaryInitResponse.students.first().id

            val currentYearResponse = authService.getCurrentYear().debugValue()
            Const.yearId = currentYearResponse.id

            val assignmentTypes = authService.getAssignmentTypes()
            Const.assignmentTitles = assignmentTypes.associate { it.id to it.name }

            encryptedPreferences.edit {
                putString(
                    PrefsKeys.ACCOUNT_NAME,
                    diaryInitResponse.students.first().name.substringAfter(' ')
                )
                putString(PrefsKeys.ACCOUNT_USERNAME, login)
                putString(PrefsKeys.ACCOUNT_PASSWORD, password)
            }

            true
        }
    }

    override suspend fun logout() {
        runCatching { authService.logout() }
    }

    override fun getAccountData(): Account? {
        val name =
            encryptedPreferences.getString(PrefsKeys.ACCOUNT_NAME, null) ?: return null
        val username =
            encryptedPreferences.getString(PrefsKeys.ACCOUNT_USERNAME, null) ?: return null
        val password =
            encryptedPreferences.getString(PrefsKeys.ACCOUNT_PASSWORD, null) ?: return null

        return Account(
            name = name,
            username = username,
            password = password
        )
    }
}