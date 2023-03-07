package io.fournkoner.netschool.ui.screens.message_receivers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiver
import io.fournkoner.netschool.domain.entities.mail.MailMessageReceiverGroup
import io.fournkoner.netschool.domain.usecases.mail.GetMessageReceiversUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.RuleBasedCollator
import javax.inject.Inject

@HiltViewModel
class MessageReceiversViewModel @Inject constructor(
    private val getMessageReceiversUseCase: GetMessageReceiversUseCase,
) : ViewModel() {

    private val fullReceiversList =
        mutableMapOf<MailMessageReceiverGroup, List<MailMessageReceiver>>()
    private var searchQuery = ""

    private val _currentGroup = MutableStateFlow(MailMessageReceiverGroup.TEACHERS)
    val currentGroup: StateFlow<MailMessageReceiverGroup> get() = _currentGroup

    private val _receivers = MutableStateFlow<Map<Char, List<MailMessageReceiver>>>(emptyMap())
    val receivers: StateFlow<Map<Char, List<MailMessageReceiver>>> get() = _receivers

    init {
        viewModelScope.launch {
            fullReceiversList[MailMessageReceiverGroup.TEACHERS] =
                getMessageReceiversUseCase(MailMessageReceiverGroup.TEACHERS).getOrElse {
                    it.printStackTrace()
                    emptyList()
                }.also {
                    if (_currentGroup.value == MailMessageReceiverGroup.TEACHERS)
                        _receivers.value = it.sortByLetters()
                }
            fullReceiversList[MailMessageReceiverGroup.STUDENTS] =
                getMessageReceiversUseCase(MailMessageReceiverGroup.STUDENTS).getOrElse {
                    it.printStackTrace()
                    emptyList()
                }.also {
                    if (_currentGroup.value == MailMessageReceiverGroup.STUDENTS)
                        _receivers.value = it.sortByLetters()
                }
        }
    }

    fun search(query: String) {
        searchQuery = query

        _receivers.value = fullReceiversList[_currentGroup.value]
            ?.filter { it.name.startsWith(query, ignoreCase = true) }
            ?.sortByLetters() ?: emptyMap()
    }

    fun updateGroup(group: MailMessageReceiverGroup) {
        _currentGroup.value = group
        _receivers.value = fullReceiversList[_currentGroup.value]
            ?.filter { it.name.startsWith(searchQuery) }
            ?.sortByLetters() ?: emptyMap()
    }

    private fun List<MailMessageReceiver>.sortByLetters(): Map<Char, List<MailMessageReceiver>> {
        val result = mutableMapOf<Char, List<MailMessageReceiver>>()

        val yoRule = "& а < б < в < г < д < е < ё < ж < з < и < й "+
                "< к < л < м < н < о < п < р < с < т < у < ф "+
                "< х < ц < ч < ш < щ < ъ < ы < ь < э < ю < я"
        val ruleBasedCollator = RuleBasedCollator(yoRule)
        sortedWith { a, b -> ruleBasedCollator.compare(a.name, b.name) }.forEach {
            val letter = it.name[0]

            if (result.containsKey(letter)) {
                result[letter] = result[letter]!! + it
            } else {
                result[letter] = listOf(it)
            }
        }
        return result
    }
}