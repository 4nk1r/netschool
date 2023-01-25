package io.fournkoner.netschool.data.repositories

import io.fournkoner.netschool.data.network.AuthService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.data.utils.hexMD5
import io.fournkoner.netschool.data.utils.toFormDataBodyString
import io.fournkoner.netschool.domain.repositories.AccountRepository

internal class AccountRepositoryImpl(
    private val authService: AuthService,
) : AccountRepository {

    override suspend fun signIn(login: String, password: String): Result<Boolean> {
        return runCatching {
            val school = authService.findSchool()
                .find { it.name == Const.SCHOOL_NAME }
                ?: throw IllegalArgumentException(
                    "${Const.SCHOOL_NAME} wasn't found on ${Const.HOST}. " +
                            "Check your private_const.properties file"
                )
            val authData = authService.getAuthData()
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

            true
        }
    }

    override suspend fun logout() {
        runCatching { authService.logout() }
    }
}