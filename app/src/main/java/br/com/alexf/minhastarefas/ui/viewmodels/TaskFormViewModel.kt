package br.com.alexf.minhastarefas.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.alexf.minhastarefas.models.Task
import br.com.alexf.minhastarefas.repositories.TasksRepository
import br.com.alexf.minhastarefas.repositories.toTask
import br.com.alexf.minhastarefas.ui.states.TaskFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class TaskFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TasksRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<TaskFormUiState> =
        MutableStateFlow(TaskFormUiState())
    val uiState = _uiState.asStateFlow()
    private val id: String? = savedStateHandle["taskId"]

    init {
        _uiState.update { currentState ->
            currentState.copy(
                onTitleChange = { title ->
                    _uiState.update {
                        it.copy(title = title)
                    }
                },
                onDescriptionChange = { description ->
                    _uiState.update {
                        it.copy(description = description)
                    }
                },
                topAppBarTitle = "Criando uma tarefa",
                onDueDateChange = { newDate ->
                    _uiState.update {
                        it.copy(dueDate = newDate.toBrazilianDateFormat())
                    }
                },
                isShowDatePickerChange = { newValue ->
                    _uiState.update {
                        it.copy(showDatePicker = newValue)
                    }
                }
            )
        }
        id?.let {
            viewModelScope.launch {
                repository.findById(id)
                    .filterNotNull()
                    .mapNotNull {
                        it.toTask()
                    }.collectLatest { task ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                topAppBarTitle = "Editando tarefa",
                                title = task.title,
                                description = task.description ?: "",
                                isDeleteEnabled = true
                            )
                        }
                    }
            }
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private fun Long?.toBrazilianDateFormat(): String? {
        val formattedDate = this?.let { date ->
            Instant.fromEpochMilliseconds(date)
                .toLocalDateTime(TimeZone.UTC)
                .date
                .format(LocalDate.Format {
                    byUnicodePattern("dd/MM/yyyy")
                })
        }
        return formattedDate
    }

    suspend fun save() {
        with(_uiState.value) {
            repository.save(
                Task(
                    id = id ?: UUID.randomUUID().toString(),
                    title = title,
                    description = description
                )
            )
        }

    }

    suspend fun delete() {
        id?.let {
            repository.delete(id)
        }
    }

}