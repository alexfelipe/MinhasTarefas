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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.alexf.minhastarefas.ui.states.TaskFormUiState
import br.com.alexf.minhastarefas.ui.theme.MinhasTarefasTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    uiState: TaskFormUiState,
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    AnimatedVisibility(uiState.showDatePicker) {
        DatePickerDialog(onDismissRequest = {
            uiState.isShowDatePickerChange(false)
        }, confirmButton = {
            Button(onClick = {
                uiState.onDueDateChange(
                    datePickerState.selectedDateMillis
                )
                uiState.isShowDatePickerChange(false)
            }) {
                Text("Selecionar")
            }
        }) {
            DatePicker(datePickerState)
        }
    }
    Column(modifier) {
        val topAppBarTitle = uiState.topAppBarTitle
        val deleteEnabled = uiState.isDeleteEnabled
        TopAppBar(
            title = {
                Text(
                    text = topAppBarTitle,
                    fontSize = 20.sp,
                )
            },
            actions = {
                if (deleteEnabled) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete task icon",
                        Modifier
                            .clip(CircleShape)
                            .clickable {
                                onDeleteClick()
                            }
                            .padding(4.dp)
                    )
                }
                Icon(
                    Icons.Filled.Done,
                    contentDescription = "Save task icon",
                    Modifier
                        .clip(CircleShape)
                        .clickable {
                            onSaveClick()
                        }
                        .padding(4.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03A9F4))
        )
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedTextField(
            uiState.dueDate ?: "",
            onValueChange = {},
            Modifier
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
                                uiState.isShowDatePickerChange(true)
                            }
                        }
                    }
                }
            }
        )
        val title = uiState.title
        val description = uiState.description
        val titleFontStyle = TextStyle.Default.copy(fontSize = 24.sp)
        val descriptionFontStyle = TextStyle.Default.copy(fontSize = 18.sp)
        BasicTextField(
            value = title,
            onValueChange = uiState.onTitleChange,
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                if (title.isEmpty()) {
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
            value = description, onValueChange = uiState.onDescriptionChange,
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                if (description.isEmpty()) {
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
            onSaveClick = {},
            onDeleteClick = {}
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
                isDeleteEnabled = true
            ),
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskFormScreenShowingDatePickerDialogPreview() {
    MinhasTarefasTheme {
        TaskFormScreen(
            uiState = TaskFormUiState(
                showDatePicker = true
            ),
            onSaveClick = {},
            onDeleteClick = {},
        )
    }
}