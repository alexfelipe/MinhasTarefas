package br.com.alexf.minhastarefas.ui.states

data class TaskFormUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: String? = null,
    val topAppBarTitle: String = "Criando uma tarefa",
)
