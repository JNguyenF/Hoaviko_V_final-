package mg.itu.hoaviko.ui.admin

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import mg.itu.hoaviko.data.dao.WithdrawalWithUser
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.data.entity.Withdrawal
import mg.itu.hoaviko.domain.WithdrawalRules
import mg.itu.hoaviko.presentation.viewmodel.AdminViewModel
import mg.itu.hoaviko.ui.components.formatAriary
import mg.itu.hoaviko.ui.components.formatDate
import mg.itu.hoaviko.ui.i18n.fmt
import mg.itu.hoaviko.ui.i18n.LocalAppStrings
import mg.itu.hoaviko.ui.i18n.text

/** Espace administrateur : demandes de retrait et gestion des comptes membres. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(snackbarHostState: SnackbarHostState) {
    val viewModel: AdminViewModel = viewModel(factory = AdminViewModel.Factory)
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var pendingAction by remember { mutableStateOf<Long?>(null) }
    var pendingApprove by remember { mutableStateOf(true) }
    var pendingDelete by remember { mutableStateOf(false) }
    var deleteReason by remember { mutableStateOf("") }

    LaunchedEffect(event) {
        val message = event?.text(strings) ?: return@LaunchedEffect
        scope.launch {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeEvent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            strings.adminTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(strings.adminRequestsTitle) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(strings.adminTabUsers) }
            )
        }
        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            1 -> UsersTab(
                users = users,
                onDelete = {
                    pendingDelete = true
                    pendingAction = it
                    deleteReason = ""
                }
            )
            else -> RequestsTab(
                requests = requests,
                onApprove = {
                    pendingApprove = true
                    pendingAction = it
                },
                onReject = {
                    pendingApprove = false
                    pendingAction = it
                }
            )
        }
    }

    val actionId = pendingAction
    if (pendingDelete) {
        val target = users.firstOrNull { it.id == actionId }
        AlertDialog(
            onDismissRequest = { pendingDelete = false },
            title = { Text(strings.deleteUserConfirmTitle) },
            text = {
                Column {
                    Text(
                        fmt(
                            strings.deleteUserConfirmHint,
                            "name" to (target?.let { "${it.lastName} ${it.firstName}" } ?: ""),
                            "username" to (target?.username ?: "")
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = deleteReason,
                        onValueChange = { deleteReason = it },
                        label = { Text(strings.deleteReasonLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = deleteReason.isNotBlank(),
                    onClick = {
                        if (actionId != null) viewModel.deleteUser(actionId)
                        pendingDelete = false
                        pendingAction = null
                        pendingApprove = true
                    }
                ) {
                    Text(strings.deleteUserButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = false }) { Text(strings.cancel) }
            }
        )
    } else if (actionId != null) {
        val target = requests.firstOrNull { it.id == actionId }
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = {
                Text(if (pendingApprove) strings.approveConfirmTitle else strings.rejectConfirmTitle)
            },
            text = {
                Text(
                    fmt(
                        if (pendingApprove) strings.approveConfirmHint else strings.rejectConfirmHint,
                        "amount" to formatAriary(target?.amountAriary ?: 0L)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pendingApprove) viewModel.approve(actionId) else viewModel.reject(actionId)
                        pendingAction = null
                    }
                ) {
                    Text(strings.ok)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) { Text(strings.cancel) }
            }
        )
    }
}

@Composable
private fun RequestsTab(
    requests: List<WithdrawalWithUser>,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit
) {
    if (requests.isEmpty()) {
        EmptyHint(LocalAppStrings.current.emptyAdminRequests)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(requests, key = { it.id }) { request ->
                AdminRequestCard(
                    request = request,
                    onApprove = { onApprove(request.id) },
                    onReject = { onReject(request.id) }
                )
            }
        }
    }
}

@Composable
private fun UsersTab(users: List<UserAccount>, onDelete: (Long) -> Unit) {
    val strings = LocalAppStrings.current
    if (users.isEmpty()) {
        EmptyHint(strings.emptyAdminUsers)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users, key = { it.id }) { user ->
                AdminUserCard(user = user, onDelete = { onDelete(user.id) })
            }
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun AdminUserCard(user: UserAccount, onDelete: () -> Unit) {
    val strings = LocalAppStrings.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${user.lastName} ${user.firstName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "@${user.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    fmt(strings.memberRank, "rank" to user.registrationRank),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (user.profession.isNotBlank()) {
                    Text(
                        user.profession,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = strings.deleteUserButton,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AdminRequestCard(
    request: WithdrawalWithUser,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val strings = LocalAppStrings.current
    val penaltyAmount = WithdrawalRules.penaltyAmount(request.amountAriary, request.penaltyPercent)
    val netAmount = WithdrawalRules.netAmount(request.amountAriary, request.penaltyPercent)
    val statusLabel = when (request.status) {
        Withdrawal.STATUS_APPROVED -> strings.statusApproved
        Withdrawal.STATUS_REJECTED -> strings.statusRejected
        else -> strings.statusPending
    }
    val statusColor = when (request.status) {
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
                        request.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatAriary(request.amountAriary),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        fmt(strings.requestedOn, "date" to formatDate(request.requestDateEpochDay)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        fmt(strings.withdrawnOn, "date" to formatDate(request.withdrawalDateEpochDay)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (request.hasNotice) strings.noticeWithLabel else strings.noticeWithoutLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        fmt(strings.penaltyLine, "percent" to request.penaltyPercent, "amount" to formatAriary(penaltyAmount)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        fmt(strings.netLine, "amount" to formatAriary(netAmount)),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (request.note.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            fmt(strings.justificationLine, "reason" to request.note),
                            style = MaterialTheme.typography.bodyMedium,
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
            if (request.status == Withdrawal.STATUS_PENDING) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onApprove, modifier = Modifier.weight(1f)) {
                        Text(strings.approveButton)
                    }
                    OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                        Text(strings.rejectButton)
                    }
                }
            }
        }
    }
}