package mg.itu.hoaviko.ui.auth

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mg.itu.hoaviko.presentation.viewmodel.AuthViewModel
import mg.itu.hoaviko.ui.Routes

/** Navigation d'authentification (hors session). */
@Composable
fun AuthScaffold(snackbarHostState: SnackbarHostState) {
    val viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.AUTH
    ) {
        composable(Routes.AUTH) { IntroScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController, viewModel, snackbarHostState) }
        composable(Routes.REGISTER) { RegisterScreen(navController, viewModel, snackbarHostState) }
        composable(Routes.FORGOT) { ForgotPasswordScreen(navController, viewModel, snackbarHostState) }
    }
}