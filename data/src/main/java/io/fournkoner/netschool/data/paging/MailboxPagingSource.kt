package io.fournkoner.netschool.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.fournkoner.netschool.data.utils.debugValue
import io.fournkoner.netschool.domain.entities.mail.MailMessageShort
import io.fournkoner.netschool.domain.entities.mail.Mailbox
import io.fournkoner.netschool.domain.usecases.mail.GetMailboxUseCase

class MailboxPagingSource @AssistedInject internal constructor(
    @Assisted private val mailbox: Mailbox,
    private val getMailboxUseCase: GetMailboxUseCase
) : PagingSource<Int, MailMessageShort>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MailMessageShort> {
        val page = params.key ?: 1
        val result = getMailboxUseCase(mailbox, page)
        val data = result.getOrElse { it.printStackTrace(); emptyList() }.debugValue()

        return if (result.isSuccess) {
            LoadResult.Page(
                data = data,
                prevKey = (page - 1).takeIf { it > 0 },
                nextKey = (page + 1).takeIf { data.isNotEmpty() }
            )
        } else {
            LoadResult.Error(result.exceptionOrNull() ?: UnknownError())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MailMessageShort>) =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    @AssistedFactory
    interface Factory {
        fun create(mailbox: Mailbox): MailboxPagingSource
    }
}