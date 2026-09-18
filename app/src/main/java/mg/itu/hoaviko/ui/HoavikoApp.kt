package mg.itu.hoaviko.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mg.itu.hoaviko.presentation.viewmodel.AuthViewModel
import mg.itu.hoaviko.presentation.viewmodel.SessionViewModel
import mg.itu.hoaviko.ui.admin.AdminScreen
import mg.itu.hoaviko.ui.auth.AuthScaffold
import mg.itu.hoaviko.ui.home.HomeScreen
import mg.itu.hoaviko.ui.i18n.EnglishStrings
import mg.itu.hoaviko.ui.i18n.FrenchStrings
import mg.itu.hoaviko.ui.i18n.LocalAppStrings
import mg.itu.hoaviko.ui.i18n.MalagasyStrings
import mg.itu.hoaviko.ui.i18n.UiMessage
import mg.itu.hoaviko.ui.profile.SettingsScreen
import mg.itu.hoaviko.ui.simulation.SimulationScreen
import mg.itu.hoaviko.ui.tracking.TrackingScreen

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private fun bottomItems(isAdmin: Boolean): List<BottomItem> = buildList {
    add(BottomItem(Routes.ACCUEIL, "Accueil", Icons.Filled.Home))
    if (!isAdmin) {
        add(BottomItem(Routes.SIMULATION, "Simulation", Icons.Filled.Star))
        add(BottomItem(Routes.SUIVI, "Suivi", Icons.Filled.List))
    }
    if (isAdmin) add(BottomItem(Routes.ADMIN, "Admin", Icons.Filled.AdminPanelSettings))
    add(BottomItem(Routes.PROFIL, "Profil", Icons.Filled.AccountCircle))
}

object Routes {
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT = "forgot"
    const val ACCUEIL = "accueil"
    const val SIMULATION = "simulation"
    const val SUIVI = "suivi"
    const val ADMIN = "admin"
    const val PROFIL = "profil"
}

/**
 * Racine de l'application.
 *
 * - Utilisateur connecté (session persistée) : affiche directement l'accueil,
 *   même après fermeture de l'application.
 * - Utilisateur déconnecté : écran de bienvenue.
 * - Après une connexion réussie : message « Connexion établie » puis l'accueil.
 */
@Composable
fun HoavikoApp() {
    val sessionViewModel: SessionViewModel = viewModel(factory = SessionViewModel.Factory)
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)

    val currentUser by sessionViewModel.currentUser.collectAsStateWithLifecycle()
    val authEvent by authViewModel.event.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Langue de l'interface selon les préférences du compte (français par défaut).
    val strings = when (currentUser?.language) {
        "en" -> EnglishStrings
        "mg" -> MalagasyStrings
        else -> FrenchStrings
    }

    // Message de connexion réussie puis bascule déjà effectuée vers l'accueil.
    LaunchedEffect(authEvent) {
        if (authEvent == UiMessage.LOGIN_SUCCESS) {
            // Consomme immédiatement pour que le prochain log in réémette bien.
            authViewModel.consumeEvent()
            val message = strings.loginSuccess
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    CompositionLocalProvider(LocalAppStrings provides strings) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                key(currentUser?.id) {
                    val account = currentUser
                    if (account != null) {
                        val isAdmin = account.role == mg.itu.hoaviko.data.entity.UserAccount.ROLE_ADMIN
                        MainScaffold(snackbarHostState, sessionViewModel::logout, isAdmin)
                    } else {
                        AuthScaffold(snackbarHostState)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(snackbarHostState: SnackbarHostState, onLogout: () -> Unit, isAdmin: Boolean) {
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.ACCUEIL,
            modifier = Modifier.weight(1f)
        ) {
            composable(Routes.ACCUEIL) {
                HomeScreen(
                    onNavigateToSimulation = { navController.navigate(Routes.SIMULATION) },
                    onNavigateToSettings = { navController.navigate(Routes.PROFIL) }
                )
            }
            if (!isAdmin) {
                composable(Routes.SIMULATION) { SimulationScreen(snackbarHostState) }
                composable(Routes.SUIVI) { TrackingScreen(snackbarHostState) }
            }
            composable(Routes.ADMIN) { AdminScreen(snackbarHostState) }
            composable(Routes.PROFIL) { SettingsScreen(snackbarHostState, onLogout) }
        }
        MainBottomBar(navController, isAdmin)
    }
}

@Composable
private fun MainBottomBar(navController: NavHostController, isAdmin: Boolean) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        bottomItems(isAdmin).forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}