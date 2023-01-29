package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.Journal

interface JournalRepository {

    suspend fun getJournal(weekStart: String, weekEnd: String): Result<Journal>
}