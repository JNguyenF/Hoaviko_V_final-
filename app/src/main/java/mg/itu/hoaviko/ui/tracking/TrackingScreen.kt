package mg.itu.hoaviko.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import mg.itu.hoaviko.data.entity.Withdrawal
import mg.itu.hoaviko.domain.HistoryPeriod
import mg.itu.hoaviko.domain.WithdrawalRules
import mg.itu.hoaviko.presentation.viewmodel.TrackingViewModel
import mg.itu.hoaviko.ui.components.AmountField
import mg.itu.hoaviko.ui.components.ChannelDropdown
import mg.itu.hoaviko.ui.components.PasswordField
import mg.itu.hoaviko.ui.components.StatCard
import mg.itu.hoaviko.ui.components.formatAriary
import mg.itu.hoaviko.ui.components.formatDate
import mg.itu.hoaviko.ui.i18n.AppStrings
import mg.itu.hoaviko.ui.i18n.fmt
import mg.itu.hoaviko.ui.i18n.LocalAppStrings
import mg.itu.hoaviko.ui.i18n.text

/** Espace Suivi : versements, historique filtré et demandes de retrait. */
@Composable
fun TrackingScreen(snackbarHostState: SnackbarHostState) {
    val viewModel: TrackingViewModel = viewModel(factory = TrackingViewModel.Factory)
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val contributions by viewModel.contributions.collectAsStateWithLifecycle()
    val totalContributed by viewModel.totalContributed.collectAsStateWithLifecycle()
    val paymentCount by viewModel.paymentCount.collectAsStateWithLifecycle()
    val period by viewModel.period.collectAsStateWithLifecycle()
    val depositForm by viewModel.depositForm.collectAsStateWithLifecycle()
    val withdrawalForm by viewModel.withdrawalForm.collectAsStateWithLifecycle()
    val myWithdrawals by viewModel.myWithdrawals.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle()
    val success by viewModel.success.collectAsStateWithLifecycle()
    val withdrawalSuccess by viewModel.withdrawalSuccess.collectAsStateWithLifecycle()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawalDialog by remember { mutableStateOf(false) }
    val userAge = viewModel.userAge

    val periodOptions = listOf(
        HistoryPeriod.ALL to strings.filterAll,
        HistoryPeriod.WEEK to strings.filterWeek,
        HistoryPeriod.MONTH to strings.filterMonth,
        HistoryPeriod.YEAR to strings.filterYear
    )

    LaunchedEffect(event) {
        val message = event?.text(strings) ?: return@LaunchedEffect
        scope.launch {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeEvent()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                strings.trackTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = strings.totalPaid,
                    value = formatAriary(totalContributed),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.paymentsCount,
                    value = paymentCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.selectDefaultChannel()
                        showDepositDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(strings.newDepositTitle)
                }
                OutlinedButton(
                    onClick = { showWithdrawalDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(strings.withdrawalButton)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                periodOptions.forEach { (option, label) ->
                    FilterChip(
                        selected = period == option,
                        onClick = { viewModel.onPeriodChange(option) },
                        label = { Text(label) }
                    )
                }
            }
        }

        if (contributions.isEmpty()) {
            item {
                Text(
                    strings.emptyHistory,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            val filtered = viewModel.filteredContributions(contributions, period)
            if (filtered.isEmpty()) {
                item {
                    Text(
                        strings.periodNoResult,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                items(filtered, key = { "c-${it.id}" }) { contribution ->
                    ContributionRow(
                        amount = contribution.amountAriary,
                        dateEpochDay = contribution.dateEpochDay,
                        channelName = channels.firstOrNull { it.id == contribution.paymentChannelId }?.name,
                        accountReference = contribution.accountReference,
                        note = contribution.note
                    )
                }
            }
        }

        item {
            Text(
                strings.myWithdrawalRequests,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (myWithdrawals.isEmpty()) {
            item {
                Text(
                    strings.emptyWithdrawals,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            items(myWithdrawals, key = { "w-${it.id}" }) { withdrawal ->
                WithdrawalRequestRow(withdrawal, strings)
            }
        }
    }

    if (showDepositDialog) {
        UserDepositDialog(
            viewModel = viewModel,
            depositForm = depositForm,
            channels = channels,
            onDismiss = { showDepositDialog = false },
            onConfirm = {
                viewModel.confirmDeposit()
                showDepositDialog = false
            }
        )
    }

    if (showWithdrawalDialog) {
        UserWithdrawalDialog(
            viewModel = viewModel,
            withdrawalForm = withdrawalForm,
            userAge = userAge,
            onDismiss = { showWithdrawalDialog = false },
            onConfirm = {
                viewModel.submitWithdrawal()
                showWithdrawalDialog = false
            }
        )
    }

    val currentSuccess = success
    if (currentSuccess != null) {
        AlertDialog(
            onDismissRequest = viewModel::consumeSuccess,
            title = { Text(strings.depositSuccessTitle) },
            text = {
                Text(fmt(strings.depositSuccessLine, "amount" to formatAriary(currentSuccess.amount), "total" to formatAriary(currentSuccess.newTotal)))
            },
            confirmButton = {
                TextButton(onClick = viewModel::consumeSuccess) { Text(strings.ok) }
            }
        )
    }

    val currentWithdrawalSuccess = withdrawalSuccess
    if (currentWithdrawalSuccess != null) {
        AlertDialog(
            onDismissRequest = viewModel::consumeWithdrawalSuccess,
            title = { Text(strings.withdrawalSuccessTitle) },
            text = {
                Text(
                    fmt(
                        strings.withdrawalSuccessLine,
                        "amount" to formatAriary(currentWithdrawalSuccess.amount),
                        "penalty" to formatAriary(currentWithdrawalSuccess.penalty),
                        "percent" to currentWithdrawalSuccess.percent,
                        "net" to formatAriary(currentWithdrawalSuccess.net)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::consumeWithdrawalSuccess) { Text(strings.ok) }
            }
        )
    }
}

@Composable
private fun UserDepositDialog(
    viewModel: TrackingViewModel,
    depositForm: mg.itu.hoaviko.presentation.viewmodel.DepositForm,
    channels: List<mg.itu.hoaviko.data.entity.PaymentChannel>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val appStrings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appStrings.depositDialogTitle) },
        text = {
            Column {
                Text(
                    appStrings.depositConfirmHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                AmountField(
                    value = depositForm.amount,
                    onValueChange = viewModel::onAmountChange,
                    label = appStrings.amountLabel
                )
                Spacer(Modifier.height(12.dp))
                ChannelDropdown(
                    channels = channels,
                    selectedId = depositForm.channelId,
                    onSelection = viewModel::onChannelChange,
                    label = appStrings.paymentModeLabel,
                    placeholder = appStrings.chooseChannel
                )
                Spacer(Modifier.height(12.dp))
                val selectedChannel = channels.firstOrNull { it.id == depositForm.channelId }
                val isBank = selectedChannel?.category == "Banque"
                OutlinedTextField(
                    value = depositForm.accountRef,
                    onValueChange = viewModel::onAccountRefChange,
                    label = {
                        Text(if (isBank) appStrings.bankAccountLabel else appStrings.phoneNumberLabel)
                    },
                    placeholder = {
                        Text(
                            if (isBank) "BRED · 0001234567" else "034 XX XXX XX"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                PasswordField(
                    value = depositForm.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = appStrings.passwordConfirmLabel
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text(appStrings.depositButton) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(appStrings.cancel) }
        }
    )
}

@Composable
private fun UserWithdrawalDialog(
    viewModel: TrackingViewModel,
    withdrawalForm: mg.itu.hoaviko.presentation.viewmodel.WithdrawalForm,
    userAge: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val appStrings = LocalAppStrings.current
    val percent = viewModel.penaltyPercentFor(withdrawalForm.hasNotice)
    val estimatedPenalty = WithdrawalRules.penaltyAmount(withdrawalForm.amount.toLongOrNull() ?: 0L, percent)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appStrings.withdrawalDialogTitle) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    appStrings.withdrawalConfirmHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                AmountField(
                    value = withdrawalForm.amount,
                    onValueChange = viewModel::onWithdrawalAmountChange,
                    label = appStrings.amountLabel
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    appStrings.noticeFieldLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = withdrawalForm.hasNotice,
                        onClick = { viewModel.onNoticeChange(true) },
                        label = { Text(appStrings.noticeWithLabel) }
                    )
                    FilterChip(
                        selected = !withdrawalForm.hasNotice,
                        onClick = { viewModel.onNoticeChange(false) },
                        label = { Text(appStrings.noticeWithoutLabel) }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    fmt(
                        appStrings.penaltyPreview,
                        "percent" to percent,
                        "amount" to formatAriary(estimatedPenalty)
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = withdrawalForm.justification,
                    onValueChange = viewModel::onWithdrawalJustificationChange,
                    label = { Text(appStrings.justificationLabel) },
                    placeholder = { Text(appStrings.justificationHint) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(Modifier.height(12.dp))
                PasswordField(
                    value = withdrawalForm.password,
                    onValueChange = viewModel::onWithdrawalPasswordChange,
                    label = appStrings.passwordConfirmLabel
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text(appStrings.withdrawalSubmitButton) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(appStrings.cancel) }
        }
    )
}

@Composable
private fun ContributionRow(
    amount: Long,
    dateEpochDay: Long,
    channelName: String?,
    accountReference: String,
    note: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    formatAriary(amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    formatDate(dateEpochDay),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (accountReference.isNotBlank()) {
                    Text(
                        accountReference,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (note.isNotBlank()) {
                    Text(
                        note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (channelName != null) {
                Text(
                    channelName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun WithdrawalRequestRow(withdrawal: Withdrawal, strings: AppStrings) {
    val statusLabel = when (withdrawal.status) {
        Withdrawal.STATUS_APPROVED -> strings.statusApproved
        Withdrawal.STATUS_REJECTED -> strings.statusRejected
        else -> strings.statusPending
    }
    val statusColor = when (withdrawal.status) {
        Withdrawal.STATUS_APPROVED -> MaterialTheme.colorScheme.primary
        Withdrawal.STATUS_REJECTED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        formatAriary(withdrawal.amountAriary),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        fmt(strings.requestedOn, "date" to formatDate(withdrawal.requestDateEpochDay)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        fmt(strings.withdrawnOn, "date" to formatDate(withdrawal.withdrawalDateEpochDay)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (withdrawal.note.isNotBlank()) {
                        Text(
                            fmt(strings.justificationLine, "reason" to withdrawal.note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    statusLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                fmt(
                    strings.penaltyLine,
                    "percent" to withdrawal.penaltyPercent,
                    "amount" to formatAriary(WithdrawalRules.penaltyAmount(withdrawal.amountAriary, withdrawal.penaltyPercent))
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                fmt(
                    strings.netLine,
                    "amount" to formatAriary(WithdrawalRules.netAmount(withdrawal.amountAriary, withdrawal.penaltyPercent))
                ),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}