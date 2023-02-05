package io.fournkoner.netschool.data.repositories

import com.google.gson.Gson
import io.fournkoner.netschool.data.models.journal.JournalAttachmentsRequest
import io.fournkoner.netschool.data.network.JournalService
import io.fournkoner.netschool.data.utils.Const
import io.fournkoner.netschool.data.utils.bringToFirst
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.domain.entities.AssignmentDetailed
import io.fournkoner.netschool.domain.entities.Journal
import io.fournkoner.netschool.domain.repositories.JournalRepository

internal class JournalRepositoryImpl(
    private val journalService: JournalService,
) : JournalRepository {

    override suspend fun getJournal(weekStart: String, weekEnd: String): Result<Journal> {
        return runCatching {
            val journalResponse = journalService.getJournal(weekStart, weekEnd).debugValue()

            val assignmentIds = Gson().toJson(JournalAttachmentsRequest(
                assignmentIds = mutableListOf<Int>().apply {
                    journalResponse.weekDays.forEach { day ->
                        day.classes.forEach { clazz ->
                            clazz.assignments.orEmpty().forEach { assignment ->
                                add(assignment.id)
                            }
                        }
                    }
                }
            )).debugValue()
            val attachmentsResponse = journalService.getAttachments(assignmentIds).debugValue()

            val overdueClassesResponse = journalService.getOverdueClasses(weekStart, weekEnd)

            Journal(
                weekStart = journalResponse.weekStart,
                weekEnd = journalResponse.weekEnd,
                days = journalResponse.weekDays.map { day ->
                    Journal.Day(
                        date = day.date,
                        classes = day.classes.map { clazz ->
                            Journal.Class(
                                position = clazz.position,
                                name = clazz.name,
                                assignments = clazz.assignments.orEmpty()
                                    .bringToFirst { it.type == 3 }
                                    .map { assignment ->
                                        Journal.Class.Assignment(
                                            id = assignment.id,
                                            name = assignment.name,
                                            attachments = attachmentsResponse
                                                .find { it.assignmentId == assignment.id }
                                                ?.attachments.orEmpty()
                                                .map { attachment ->
                                                    Journal.Class.Assignment.Attachment(
                                                        name = attachment.fileName,
                                                        file = "${Const.HOST}webapi/attachments/${attachment.id}"
                                                    )
                                                },
                                            grade = assignment.grade?.mark,
                                            type = when (assignment.type) {
                                                3 -> Journal.Class.Assignment.Type.Homework
                                                5 -> Journal.Class.Assignment.Type.IndependentWork
                                                10 -> Journal.Class.Assignment.Type.Answer
                                                else -> Journal.Class.Assignment.Type.Unknown
                                            }
                                        )
                                    },
                                grades = clazz.assignments.orEmpty()
                                    .filter { it.grade != null }
                                    .map { it.grade!!.mark }
                            )
                        }.sortedBy { it.position }
                    )
                },
                overdueClasses = overdueClassesResponse.map { clazz ->
                    Journal.OverdueClass(
                        subject = clazz.subject,
                        name = clazz.name,
                        due = clazz.due
                    )
                }
            )
        }
    }

    override suspend fun getDetailedAssignments(
        assignments: List<Journal.Class.Assignment>
    ): Result<List<AssignmentDetailed>> {
        return runCatching {
            assignments.map { assignment ->
                val detailed = journalService.getAttachmentDetailed(assignment.id)
                AssignmentDetailed(
                    name = detailed.name,
                    description = detailed.description,
                    attachments = detailed.attachments.map { attachment ->
                        AssignmentDetailed.Attachment(
                            name = attachment.name,
                            file = "${Const.HOST}webapi/attachments/${attachment.id}"
                        )
                    },
                    teacher = detailed.teachers.first().name,
                    subject = detailed.subject.name,
                    type = assignment.type,
                    grade = assignment.grade
                ).debugValue()
            }
        }
    }
}