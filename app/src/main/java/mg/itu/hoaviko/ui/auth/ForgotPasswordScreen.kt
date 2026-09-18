package mg.itu.hoaviko.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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

/** Réinitialisation du mot de passe (vérification par e-mail + CIN). */
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState
) {
    val strings = LocalAppStrings.current
    val form by viewModel.forgotForm.collectAsStateWithLifecycle()

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
                strings.forgotTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            strings.forgotSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = form.email,
            onValueChange = viewModel::onForgotEmail,
            label = { Text(strings.forgotEmailLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = form.cinNumber,
            onValueChange = viewModel::onForgotCin,
            label = { Text(strings.forgotCinLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = form.newPassword,
            onValueChange = viewModel::onForgotNewPassword,
            label = strings.newPasswordLabel
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = form.confirmPassword,
            onValueChange = viewModel::onForgotConfirm,
            label = strings.confirmNewPasswordLabel
        )

        Spacer(Modifier.height(20.dp))
        Button(onClick = viewModel::resetPassword, modifier = Modifier.fillMaxWidth()) {
            Text(strings.forgotSubmit)
        }
    }
}