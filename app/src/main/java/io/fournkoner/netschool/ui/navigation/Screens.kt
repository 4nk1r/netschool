package io.fournkoner.netschool.ui.navigation

import androidx.annotation.StringRes
import io.fournkoner.netschool.R

enum class Screen(
    val route: String,
    @StringRes val resourceId: Int? = null,
) {

    Auth("auth"),
    Journal("journal", R.string.bn_journal),
    Reports("reports", R.string.bn_reports),
    Mail("mail", R.string.bn_mail),
    Schedule("schedule", R.string.bn_schedule),
}
