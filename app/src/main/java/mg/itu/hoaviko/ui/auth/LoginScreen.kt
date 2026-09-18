package mg.itu.hoaviko.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import mg.itu.hoaviko.presentation.viewmodel.AuthViewModel
import mg.itu.hoaviko.ui.Routes
import mg.itu.hoaviko.ui.components.PasswordField
import mg.itu.hoaviko.ui.i18n.LocalAppStrings

/** Connexion par nom d'utilisateur/e-mail et mot de passe. */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState
) {
    val strings = LocalAppStrings.current
    val form by viewModel.loginForm.collectAsStateWithLifecycle()

    AuthEventSnackbar(viewModel, snackbarHostState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate(Routes.AUTH) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
            }
            Text(
                strings.loginScreenTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = form.identifier,
            onValueChange = viewModel::onLoginIdentifierChange,
            label = { Text(strings.identifierLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = form.password,
            onValueChange = viewModel::onLoginPasswordChange,
            label = strings.passwordLabel
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { navController.navigate(Routes.FORGOT) },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(strings.forgotPassword)
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = viewModel::login, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(strings.loginSubmit)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                strings.registerLink,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                Text(strings.registerScreenTitle)
            }
        }
    }
}