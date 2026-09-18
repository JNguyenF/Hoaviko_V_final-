package mg.itu.hoaviko.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import mg.itu.hoaviko.presentation.viewmodel.AuthViewModel
import mg.itu.hoaviko.ui.Routes
import mg.itu.hoaviko.ui.components.DateField
import mg.itu.hoaviko.ui.components.PasswordField
import mg.itu.hoaviko.ui.i18n.LocalAppStrings

/** Création de compte : identité, dates, contact et mot de passe. */
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState
) {
    val strings = LocalAppStrings.current
    val form by viewModel.registerForm.collectAsStateWithLifecycle()
    var showPolicy by remember { mutableStateOf(false) }

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
                strings.registerScreenTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = form.lastName,
            onValueChange = viewModel::onRegisterLastName,
            label = { Text(strings.lastNameLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = form.firstName,
            onValueChange = viewModel::onRegisterFirstName,
            label = { Text(strings.firstNameLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        DateField(
            epochDay = form.birthDateEpochDay,
            onSelect = viewModel::onRegisterBirth,
            label = strings.birthDateLabel
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = form.cinNumber,
            onValueChange = viewModel::onRegisterCin,
            label = { Text(strings.cinLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = form.profession,
            onValueChange = viewModel::onRegisterProfession,
            label = { Text(strings.professionLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = form.email,
            onValueChange = viewModel::onRegisterEmail,
            label = { Text(strings.emailLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = form.password,
            onValueChange = viewModel::onRegisterPassword,
            label = strings.createPasswordLabel
        )
        Spacer(Modifier.height(12.dp))
        PasswordField(
            value = form.confirmPassword,
            onValueChange = viewModel::onRegisterConfirm,
            label = strings.confirmPasswordLabel
        )

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = form.acceptedPolicy,
                onCheckedChange = viewModel::onRegisterPolicyChange
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    strings.acceptPolicyLabel,
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { showPolicy = true }) {
                    Text(
                        strings.readPolicyLink,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = viewModel::register, modifier = Modifier.fillMaxWidth()) {
            Text(strings.registerSubmit)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            strings.passwordConfirmLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showPolicy) {
        AlertDialog(
            onDismissRequest = { showPolicy = false },
            title = { Text(strings.policyTitle) },
            text = {
                val policyLines = remember(strings.policyBody) { strings.policyBody.split('\n') }
                val policyText = buildAnnotatedString {
                    policyLines.forEachIndexed { index, line ->
                        val isHeading = Regex("^\\d+\\.\\s").containsMatchIn(line)
                        withStyle(
                            SpanStyle(fontWeight = if (isHeading) FontWeight.Bold else FontWeight.Normal)
                        ) {
                            append(line)
                        }
                        if (index < policyLines.lastIndex) append('\n')
                    }
                }
                Text(
                    policyText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(onClick = { showPolicy = false }) { Text(strings.ok) }
            }
        )
    }
}