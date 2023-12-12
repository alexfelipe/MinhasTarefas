package br.com.alexf.minhastarefas.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.alexf.minhastarefas.models.Task
import br.com.alexf.minhastarefas.ui.screens.TaskFormScreen
import br.com.alexf.minhastarefas.ui.viewmodels.TaskFormViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

const val taskFormRoute = "taskForm"
const val taskIdArgument = "taskId"

fun NavGraphBuilder.taskFormScreen(
    onPopBackStack: () -> Unit,
) {
    composable("$taskFormRoute?$taskIdArgument={$taskIdArgument}") {
        val taskId = navArgument(taskIdArgument) {
            nullable = true
        }
        val scope = rememberCoroutineScope()
        val viewModel = koinViewModel<TaskFormViewModel>(
            parameters = { parametersOf(taskId) })
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(uiState.isSavedOrDeleted) {
            if (uiState.isSavedOrDeleted) {
                onPopBackStack()
            }
        }
        TaskFormScreen(
            uiState = uiState,
            onSaveClick = {
                scope.launch {
                    viewModel.save()
                }
            },
            onDeleteClick = {
                scope.launch {
                    viewModel.delete()
                }
            })
    }
}

fun NavHostController.navigateToNewTaskForm(navOptions: NavOptions? = null) {
    navigate(taskFormRoute, navOptions)
}

fun NavHostController.navigateToEditTaskForm(task: Task) {
    navigate("$taskFormRoute?$taskIdArgument=${task.id}")
}