package br.com.alexf.minhastarefas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.alexf.minhastarefas.ui.states.TaskFormUiState
import br.com.alexf.minhastarefas.ui.theme.MinhasTarefasTheme
import br.com.alexf.minhastarefas.ui.viewmodels.TaskFormEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    uiState: TaskFormUiState,
    modifier: Modifier = Modifier,
    onEvent: (TaskFormEvent) -> Unit
) {

    val datePickerState = rememberDatePickerState()

    var isDatePickerOpen by remember { mutableStateOf(false) }

    var isDeleteEnabled by remember { mutableStateOf(true) }

    AnimatedVisibility(isDatePickerOpen) {
        DatePickerDialog(
            onDismissRequest = { isDatePickerOpen = false },
            confirmButton = {
                Button(
                    onClick = {
                        onEvent(
                            TaskFormEvent.OnDueDateChange(datePickerState.selectedDateMillis)
                        )
                        isDatePickerOpen = false
                    }
                ) {
                    Text("Selecionar")
                }
            },
            content = { DatePicker(datePickerState) }
        )
    }

    Column(modifier) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.topAppBarTitle,
                    fontSize = 20.sp,
                )
            },
            actions = {
                if (isDeleteEnabled) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete task icon",
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onEvent(TaskFormEvent.OnDelete) }
                            .padding(4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Save task icon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onEvent(TaskFormEvent.OnSave) }
                        .padding(4.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03A9F4))
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            value = uiState.dueDate ?: "",
            onValueChange = {},
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            label = {
                Text("Due date")
            },
            readOnly = true,
            interactionSource = remember {
                MutableInteractionSource()
            }.also {
                LaunchedEffect(it.interactions) {
                    it.interactions.collectLatest { interaction ->
                        when (interaction) {
                            is PressInteraction.Release -> {
                                isDatePickerOpen = true
                            }
                        }
                    }
                }
            }
        )

        val titleFontStyle = TextStyle.Default.copy(fontSize = 24.sp)
        val descriptionFontStyle = TextStyle.Default.copy(fontSize = 18.sp)

        BasicTextField(
            value = uiState.title,
            onValueChange = { value -> onEvent(TaskFormEvent.OnTitleChange(value)) },
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                if (uiState.title.isEmpty()) {
                    Text(
                        text = "Title",
                        style = titleFontStyle.copy(
                            color = Color.Gray.copy(alpha = 0.5f)
                        ),
                    )
                }
                innerTextField()
            },
            textStyle = titleFontStyle
        )
        Spacer(modifier = Modifier.size(16.dp))
        BasicTextField(
            value = uiState.description, onValueChange = { value -> onEvent(TaskFormEvent.OnDescriptionChange(value)) },
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                if (uiState.description.isEmpty()) {
                    Text(
                        text = "Description",
                        style = descriptionFontStyle
                            .copy(
                                color = Color.Gray.copy(alpha = 0.5f)
                            )
                    )
                }
                innerTextField()
            },
            textStyle = descriptionFontStyle
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TaskFormScreenPreview() {
    MinhasTarefasTheme {
        TaskFormScreen(
            uiState = TaskFormUiState(
                topAppBarTitle = "Criando tarefa"
            ),
            onEvent = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskFormScreenWithEditModePreview() {
    MinhasTarefasTheme {
        TaskFormScreen(
            uiState = TaskFormUiState(
                topAppBarTitle = "Editando tarefa",
            ),
            onEvent = { }
        )
    }
}