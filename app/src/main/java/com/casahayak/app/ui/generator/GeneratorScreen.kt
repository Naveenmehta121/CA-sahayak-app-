package com.casahayak.app.ui.generator

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casahayak.app.data.prompt.PromptTemplates
import com.casahayak.app.ui.components.AdBanner
import com.casahayak.app.util.Constants

/**
 * Core feature screen: user enters text, gets AI-generated document back.
 * Supports all 4 feature types via the [featureType] argument from NavGraph.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    featureType: String,
    viewModel: GeneratorViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val featureLabel = PromptTemplates.getFeatureLabel(featureType)
    var inputText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Load feature info on entry
    LaunchedEffect(featureType) {
        viewModel.loadFeatureInfo(featureType)
    }

    // Show error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            if (!uiState.isLimitReached) {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = featureLabel,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        uiState.remainingUses?.let { remaining ->
                            Text(
                                text = "$remaining uses remaining this month",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (remaining <= 1)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearResult()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Limit reached dialog
            if (uiState.isLimitReached) {
                LimitReachedCard(
                    featureLabel = featureLabel,
                    onUpgrade = onNavigateToUpgrade,
                    onDismiss = viewModel::clearError
                )
            }

            // Input section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = inputPlaceholder(featureType),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = {
                            Text(
                                inputHint(featureType),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 15
                    )
                    Button(
                        onClick = {
                            viewModel.generate(featureType, inputText)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !uiState.isLoading && inputText.isNotBlank() && !uiState.isLimitReached,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating with AI...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            // AdBanner for free users
            if (uiState.subscriptionType == Constants.PLAN_FREE) {
                AdBanner(modifier = Modifier.fillMaxWidth())
            }

            // Result section
            AnimatedVisibility(
                visible = uiState.generatedText != null,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                uiState.generatedText?.let { result ->
                    ResultCard(
                        result = result,
                        isSaved = uiState.isSaved,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(result))
                            snackbarHostState.let { /* show via coroutine */ }
                        },
                        onSave = { viewModel.saveResponse(featureType, inputText) },
                        onRegenerate = { viewModel.generate(featureType, inputText) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ResultCard(
    result: String,
    isSaved: Boolean,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    onRegenerate: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AI Generated",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                if (isSaved) {
                    Text(
                        text = "✓ Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            HorizontalDivider()

            Text(
                text = result,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider()

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(result))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onSave,
                    enabled = !isSaved,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSaved) "Saved" else "Save", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onRegenerate,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retry", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun LimitReachedCard(
    featureLabel: String,
    onUpgrade: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Monthly Limit Reached",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "You've used all your free $featureLabel uses for this month. Upgrade to Premium for unlimited access.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onUpgrade,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Upgrade to Premium — ₹999/month")
            }
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

private fun inputPlaceholder(featureType: String): String = when (featureType) {
    Constants.FEATURE_NOTICE_REPLY -> "Paste the Income Tax notice details:"
    Constants.FEATURE_GST_EXPLANATION -> "Describe the GST situation:"
    Constants.FEATURE_CLIENT_REPLY -> "What did your client ask or say?"
    Constants.FEATURE_ENGAGEMENT_LETTER -> "Describe the scope of services:"
    else -> "Enter your details:"
}

private fun inputHint(featureType: String): String = when (featureType) {
    Constants.FEATURE_NOTICE_REPLY -> "e.g., Received notice u/s 143(1) for AY 2023-24. Amount demanded: ₹45,000. Reason: mismatch in Form 16 and ITR..."
    Constants.FEATURE_GST_EXPLANATION -> "e.g., My client runs a restaurant. They received a GST notice for ITC reversal on exempt supplies..."
    Constants.FEATURE_CLIENT_REPLY -> "e.g., Client asked: 'When should I file my ITR? What documents do I need?'"
    Constants.FEATURE_ENGAGEMENT_LETTER -> "e.g., New client: ABC Pvt. Ltd. Services: GST filing, TDS, and annual audit. Fee: ₹60,000/year..."
    else -> "Enter the details here..."
}
