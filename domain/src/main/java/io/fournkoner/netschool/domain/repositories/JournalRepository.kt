package io.fournkoner.netschool.domain.repositories

import io.fournkoner.netschool.domain.entities.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.Journal

interface JournalRepository {

    suspend fun getJournal(weekStart: String, weekEnd: String): Result<Journal>

    suspend fun getDetailedAssignments(
        assignments: List<Journal.Class.Assignment>
    ): Result<List<AssignmentDetailed>>
}