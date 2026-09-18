package mg.itu.hoaviko.ui.simulation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mg.itu.hoaviko.presentation.viewmodel.SimulationViewModel
import mg.itu.hoaviko.ui.components.AmountField
import mg.itu.hoaviko.ui.components.ChannelDropdown
import mg.itu.hoaviko.ui.components.ProjectionTable
import mg.itu.hoaviko.ui.components.StatCard
import mg.itu.hoaviko.ui.components.formatAriary
import mg.itu.hoaviko.ui.i18n.fmt
import mg.itu.hoaviko.ui.i18n.LocalAppStrings

/** Simulation du capital de retraite. Les âges sont fixes (naissance, retraite à 60 ans). */
@Composable
fun SimulationScreen(snackbarHostState: SnackbarHostState) {
    val viewModel: SimulationViewModel = viewModel(factory = SimulationViewModel.Factory)
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    val input by viewModel.input.collectAsStateWithLifecycle()
    val banks by viewModel.banks.collectAsStateWithLifecycle()
    val result by viewModel.result.collectAsStateWithLifecycle()
    val ageNow by viewModel.ageNow.collectAsStateWithLifecycle()
    val retirementAge = viewModel.retirementAge

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            strings.simTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            strings.simSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = if (ageNow > 0) ageNow.toString() else input.currentAge,
            onValueChange = {},
            readOnly = true,
            label = { Text(strings.ageNowLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = false
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = retirementAge.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(strings.ageRetirementLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = false
        )
        Spacer(Modifier.height(12.dp))
        AmountField(input.monthlyContribution, viewModel::onMonthlyChange, strings.monthlyLabel)
        Spacer(Modifier.height(12.dp))
        AmountField(input.initialSavings, viewModel::onInitialSavingsChange, strings.initialLabel)
        Spacer(Modifier.height(12.dp))
        ChannelDropdown(
            channels = banks,
            selectedId = input.savingsChannelId,
            onSelection = viewModel::onSavingsChannelChange,
            label = strings.institutionLabel,
            placeholder = strings.chooseChannel
        )

        result?.let { summary ->
            Spacer(Modifier.height(24.dp))
            StatCard(
                title = fmt(strings.resultTitle, "years" to (summary.months / 12)),
                value = formatAriary(summary.projectedCapital)
            )
            Spacer(Modifier.height(12.dp))
            ProjectionTable(summary.yearlyProjection)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.applyToProfile()
                    scope.launch { snackbarHostState.showSnackbar(strings.msgSettingsSaved) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.applyButton)
            }
        } ?: run {
            Spacer(Modifier.height(24.dp))
            Text(
                strings.invalidInputHint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}