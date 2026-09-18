package mg.itu.hoaviko.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.domain.RETIREMENT_AGE
import mg.itu.hoaviko.domain.WITHDRAWAL_AGE
import mg.itu.hoaviko.presentation.viewmodel.SettingsViewModel
import mg.itu.hoaviko.ui.components.AmountField
import mg.itu.hoaviko.ui.components.ChannelDropdown
import mg.itu.hoaviko.ui.components.PasswordField
import mg.itu.hoaviko.ui.i18n.UiMessage
import mg.itu.hoaviko.ui.i18n.fmt
import mg.itu.hoaviko.ui.i18n.LocalAppStrings
import mg.itu.hoaviko.ui.i18n.text

/** Mon profil : identifiants, langue, modes de paiement, objectif, déconnexion. */
@Composable
fun SettingsScreen(snackbarHostState: SnackbarHostState, onLogout: () -> Unit) {
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val user by viewModel.user.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle()

    var objectiveText by remember(user?.objectiveAmount) { mutableStateOf(user?.objectiveAmount?.toString() ?: "") }
    var monthlyText by remember(user?.monthlyContribution) { mutableStateOf(user?.monthlyContribution?.toString() ?: "") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showChangeDialog by remember { mutableStateOf(false) }

    val banks = channels.filter { it.category == "Banque" }

    LaunchedEffect(event) {
        val message = event?.text(strings) ?: return@LaunchedEffect
        viewModel.consumeEvent()
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
        if (event == UiMessage.PASSWORD_CHANGED) {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            strings.settingsTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        user?.let { account ->
            Spacer(Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoLine(strings.usernameLabel, account.username)
                    Spacer(Modifier.height(8.dp))
                    InfoLine(strings.fullNameLabel, account.fullName)
                    if (account.role != UserAccount.ROLE_ADMIN) {
                        Spacer(Modifier.height(8.dp))
                        InfoLine(strings.professionValueLabel, account.profession)
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(strings.languageLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = user?.language == "fr",
                onClick = { viewModel.setLanguage("fr") },
                label = { Text(strings.langFr) }
            )
            FilterChip(
                selected = user?.language == "en",
                onClick = { viewModel.setLanguage("en") },
                label = { Text(strings.langEn) }
            )
            FilterChip(
                selected = user?.language == "mg",
                onClick = { viewModel.setLanguage("mg") },
                label = { Text(strings.langMg) }
            )
        }

        if (user?.role != UserAccount.ROLE_ADMIN) {
            Spacer(Modifier.height(20.dp))
            Text(strings.paymentModeLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            ChannelDropdown(
                channels = channels,
                selectedId = user?.paymentChannelId ?: 0L,
                onSelection = viewModel::setPaymentChannel,
                label = strings.paymentModeLabel,
                placeholder = strings.chooseChannel
            )

            Spacer(Modifier.height(20.dp))
            Text(strings.savingsBankLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            ChannelDropdown(
                channels = banks,
                selectedId = user?.savingsChannelId ?: 0L,
                onSelection = viewModel::setSavingsChannel,
                label = strings.savingsBankLabel,
                placeholder = strings.chooseChannel
            )

            Spacer(Modifier.height(20.dp))
            Text(strings.objectiveField, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            AmountField(objectiveText, { objectiveText = it }, strings.objectiveField)
            Spacer(Modifier.height(12.dp))
            AmountField(monthlyText, { monthlyText = it }, strings.monthlyField)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (viewModel.saveObjective(objectiveText, monthlyText)) {
                        scope.launch { snackbarHostState.showSnackbar(strings.msgSettingsSaved) }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.saveSettingsButton)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(strings.changePasswordButton, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        PasswordField(currentPassword, { currentPassword = it }, strings.currentPasswordLabel)
        Spacer(Modifier.height(12.dp))
        PasswordField(newPassword, { newPassword = it }, strings.newPasswordLabel)
        Spacer(Modifier.height(12.dp))
        PasswordField(confirmPassword, { confirmPassword = it }, strings.confirmNewPasswordLabel)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                val error = viewModel.passwordChangeError(currentPassword, newPassword, confirmPassword)
                if (error == null) {
                    showChangeDialog = true
                } else {
                    viewModel.reportMessage(error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.changePasswordButton)
        }

        if (user?.role != UserAccount.ROLE_ADMIN) {
            Spacer(Modifier.height(16.dp))
            Text(
                fmt(strings.retirementInfo, "age" to RETIREMENT_AGE, "access" to WITHDRAWAL_AGE),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.logoutButton, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showChangeDialog) {
        AlertDialog(
            onDismissRequest = { showChangeDialog = false },
            title = { Text(strings.changePasswordConfirmTitle) },
            text = { Text(strings.changePasswordConfirmHint) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.changePassword(newPassword)
                        showChangeDialog = false
                    }
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}