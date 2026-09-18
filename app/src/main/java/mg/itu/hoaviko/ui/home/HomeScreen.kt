package mg.itu.hoaviko.ui.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mg.itu.hoaviko.data.entity.UserAccount
import mg.itu.hoaviko.domain.RETIREMENT_AGE
import mg.itu.hoaviko.presentation.viewmodel.HomeViewModel
import mg.itu.hoaviko.ui.components.StatCard
import mg.itu.hoaviko.ui.components.formatAriary
import mg.itu.hoaviko.ui.i18n.fmt
import mg.itu.hoaviko.ui.i18n.LocalAppStrings

/** Accueil connecté : salutation, objectif, colonnes de synthèse. */
@Composable
fun HomeScreen(
    onNavigateToSimulation: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val strings = LocalAppStrings.current

    val user by viewModel.user.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val totalContributed by viewModel.totalContributed.collectAsStateWithLifecycle()
    val paymentCount by viewModel.paymentCount.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val memberCount by viewModel.memberCount.collectAsStateWithLifecycle()
    val withdrawalCount by viewModel.withdrawalCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        user?.let { account ->
            Text(
                fmt(strings.greeting, "name" to account.firstName),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            if (account.role != UserAccount.ROLE_ADMIN) {
                Text(
                    fmt(strings.memberRank, "rank" to account.registrationRank),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (user?.role == UserAccount.ROLE_ADMIN) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = strings.adminTabUsers,
                    value = memberCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.adminRequestsTitle,
                    value = withdrawalCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.editSettingsButton)
            }

            Spacer(Modifier.height(16.dp))
        } else {
            if (user?.objectiveAmount == null || user!!.objectiveAmount > 0) {
                val objective = user?.objectiveAmount ?: 0
                StatCard(
                    title = strings.objectiveLabel,
                    value = if (objective > 0) {
                        fmt(
                            strings.objectiveProgress,
                            "current" to formatAriary(totalContributed),
                            "target" to formatAriary(objective)
                        )
                    } else formatAriary(totalContributed)
                )
            } else {
                StatCard(title = strings.totalPaid, value = formatAriary(totalContributed))
            }

            Spacer(Modifier.height(12.dp))
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

            Spacer(Modifier.height(12.dp))
            StatCard(
                title = fmt(strings.capitalProjected, "age" to RETIREMENT_AGE),
                value = summary?.let { formatAriary(it.projectedCapital) } ?: "—"
            )

            Spacer(Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        strings.monthlyContribution,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        user?.let { formatAriary(it.monthlyContribution) } ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (user?.objectiveAmount == 0L) {
                Spacer(Modifier.height(12.dp))
                Text(
                    strings.defineObjectiveHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onNavigateToSimulation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.simulateButton)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.editSettingsButton)
            }

            Spacer(Modifier.height(16.dp))
            val bankName = channels.firstOrNull { it.id == user?.savingsChannelId }?.let {
                if (it.annualRate > 0.0) "${it.name} (${formatRate(it.annualRate)})" else it.name
            }
            if (bankName != null) {
                Text(
                    bankName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun formatRate(rate: Double): String = "${(rate * 100).toInt()} %"