package br.com.alexf.minhastarefas.ui.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import br.com.alexf.minhastarefas.models.Task
import br.com.alexf.minhastarefas.ui.screens.TaskFormScreen
import br.com.alexf.minhastarefas.ui.viewmodels.TaskFormEvent
import br.com.alexf.minhastarefas.ui.viewmodels.TaskFormViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class TaskFormRoute(val taskId: String? = null)

fun NavGraphBuilder.taskFormScreen(
    onPopBackStack: () -> Unit,
) {
    composable<TaskFormRoute> { backStackEntry ->
        val taskId = backStackEntry.toRoute<TaskFormRoute>().taskId
        val viewModel = koinViewModel<TaskFormViewModel>(
            parameters = { parametersOf(taskId) })
        val uiState by viewModel.uiState.collectAsState()
        TaskFormScreen(
            uiState = uiState,
            onEvent = { event ->
                if (event == TaskFormEvent.OnDelete || event == TaskFormEvent.OnSave ) {
                    viewModel.onEvent(event)
                    onPopBackStack()
                } else {
                    viewModel.onEvent(event)
                }
            }
        )
    }
}