package br.com.alexf.minhastarefas.ui.states

data class TaskFormUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: String? = null,
    val topAppBarTitle: String = "",
    val onTitleChange: (String) -> Unit = {},
    val onDescriptionChange: (String) -> Unit = {},
    val onDueDateChange: (Long?) -> Unit = {},
    val isDeleteEnabled: Boolean = false,
    val showDatePicker: Boolean = false,
    val isShowDatePickerChange: (Boolean) -> Unit = {},
)
