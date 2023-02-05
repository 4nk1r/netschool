package io.fournkoner.netschool.ui.navigation

import androidx.annotation.StringRes
import com.google.gson.Gson
import io.fournkoner.netschool.R
import io.fournkoner.netschool.domain.entities.Journal.Class.Assignment

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int? = null,
) {

    object Auth : Screen("auth")
    object Journal : Screen("journal", R.string.bn_journal)
    object Reports : Screen("reports", R.string.bn_reports)
    object Mail : Screen("mail", R.string.bn_mail)
    object Schedule : Screen("schedule", R.string.bn_schedule)

    class AssignmentInfo(assignments: List<Assignment>) :
        Screen("$route?$NAV_ARG_ASSIGNMENT=${Gson().toJson(assignments.map { it.toParcelable() })}") {

        companion object {

            const val route = "info"
        }
    }
}
