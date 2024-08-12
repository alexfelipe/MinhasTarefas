package br.com.alexf.minhastarefas.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.alexf.minhastarefas.models.Task
import br.com.alexf.minhastarefas.repositories.TasksRepository
import br.com.alexf.minhastarefas.repositories.toTask
import br.com.alexf.minhastarefas.ui.states.TaskFormUiState
import br.com.alexf.minhastarefas.utils.toBrazilianDateFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed class TaskFormEvent {
    data class OnTitleChange(val title: String): TaskFormEvent()
    data class OnDescriptionChange(val description: String): TaskFormEvent()
    data class OnDueDateChange(val newDate: Long?): TaskFormEvent()
    data object OnDelete: TaskFormEvent()
    data object OnSave: TaskFormEvent()
}

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

    fun onEvent(event: TaskFormEvent) =
        when(event) {
            is TaskFormEvent.OnTitleChange -> onTitleChange(event.title)
            is TaskFormEvent.OnDescriptionChange -> onDescriptionChange(event.description)
            is TaskFormEvent.OnDueDateChange -> onDuoDateChange(event.newDate)
            is TaskFormEvent.OnSave -> save()
            is TaskFormEvent.OnDelete -> delete()
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

    private fun onTitleChange(title: String) {
        uiState.update {
            it.copy(title = title)
        }
    }

    private fun onDescriptionChange(description: String) {
        uiState.update {
            it.copy(description = description)
        }
    }

    private fun onDuoDateChange(newDate: Long?) {
        uiState.update {
            it.copy(dueDate = newDate.toBrazilianDateFormat())
        }
    }

    private fun save() {
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

    private fun delete() {
        viewModelScope.launch {
            id?.let {
                repository.delete(id)
            }
        }
    }

}