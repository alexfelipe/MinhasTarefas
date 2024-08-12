package br.com.alexf.minhastarefas.ui.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.alexf.minhastarefas.models.Task
import br.com.alexf.minhastarefas.repositories.TasksRepository
import br.com.alexf.minhastarefas.repositories.toTask
import br.com.alexf.minhastarefas.ui.states.TaskFormUiState
import br.com.alexf.minhastarefas.utils.toBrazilianDateFormat
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

    var uiState = MutableStateFlow(TaskFormUiState())
        private set

    private val id: String? = savedStateHandle["taskId"]

    init {
        checkTaskId()
    }

    private fun checkTaskId() {
        id?.let {
            viewModelScope.launch {
                repository.findById(id)
                    .filterNotNull()
                    .mapNotNull {
                        it.toTask()
                    }.collectLatest { task ->
                        uiState.update { currentState ->
                            currentState.copy(
                                topAppBarTitle = "Editando tarefa",
                                title = task.title,
                                description = task.description ?: "",
                            )
                        }
                    }
            }
        }
    }

    fun onTitleChange(title: String) {
        uiState.update {
            it.copy(title = title)
        }
    }

    fun onDescriptionChange(description: String) {
        uiState.update {
            it.copy(description = description)
        }
    }

    fun onDuoDateChange(newDate: Long?) {
        uiState.update {
            it.copy(dueDate = newDate.toBrazilianDateFormat())
        }
    }

    fun save() {
        viewModelScope.launch {
            with(uiState.value) {
                repository.save(
                    Task(
                        id = id ?: UUID.randomUUID().toString(),
                        title = title,
                        description = description
                    )
                )
            }
        }

    }

    fun delete() {
        viewModelScope.launch {
            id?.let {
                repository.delete(id)
            }
        }
    }

}