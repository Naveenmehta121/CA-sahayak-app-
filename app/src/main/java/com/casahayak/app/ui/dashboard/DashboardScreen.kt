package com.casahayak.app.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.casahayak.app.data.model.User
import com.casahayak.app.ui.components.AdBanner
import com.casahayak.app.util.Constants

data class FeatureCard(
    val featureType: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

/**
 * Main dashboard with 4 feature cards and subscription status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToGenerator: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAccount: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CA Sahayak",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        uiState.user?.let { user ->
                            Text(
                                text = when (user.subscriptionType) {
                                    User.TRIAL -> "Trial • ${uiState.trialDaysRemaining} days left"
                                    User.PREMIUM -> "Premium ✦"
                                    else -> "Free Plan"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = when (user.subscriptionType) {
                                    User.PREMIUM -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToAccount) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val isPremium = uiState.user?.subscriptionType == User.PREMIUM

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Upgrade banner for free/trial users approaching limit
            if (!isPremium) {
                item {
                    UpgradeBanner(
                        subscriptionType = uiState.user?.subscriptionType ?: User.FREE,
                        trialDaysRemaining = uiState.trialDaysRemaining,
                        onUpgrade = onNavigateToUpgrade
                    )
                }
            }

            // AdMob banner for free users
            if (uiState.user?.subscriptionType == User.FREE) {
                item {
                    AdBanner(modifier = Modifier.fillMaxWidth())
                }
            }

            // Feature cards header
            item {
                Text(
                    text = "Generate Documents",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Feature cards (2 columns)
            item {
                val features = listOf(
                    FeatureCard(
                        featureType = Constants.FEATURE_NOTICE_REPLY,
                        title = "Notice Reply",
                        subtitle = "Income Tax",
                        icon = Icons.Default.Gavel,
                        gradient = listOf(Color(0xFF4E3DC4), Color(0xFF6B5FD6))
                    ),
                    FeatureCard(
                        featureType = Constants.FEATURE_GST_EXPLANATION,
                        title = "GST Explanation",
                        subtitle = "Client-ready",
                        icon = Icons.Default.Receipt,
                        gradient = listOf(Color(0xFF1B6B3A), Color(0xFF2E9151))
                    ),
                    FeatureCard(
                        featureType = Constants.FEATURE_CLIENT_REPLY,
                        title = "Client Reply",
                        subtitle = "WhatsApp ready",
                        icon = Icons.Default.Chat,
                        gradient = listOf(Color(0xFF866000), Color(0xFFA67800))
                    ),
                    FeatureCard(
                        featureType = Constants.FEATURE_ENGAGEMENT_LETTER,
                        title = "Engagement Letter",
                        subtitle = "ICAI compliant",
                        icon = Icons.Default.Article,
                        gradient = listOf(Color(0xFF8B2252), Color(0xFFB3427A))
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    features.chunked(2).forEach { rowFeatures ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowFeatures.forEach { feature ->
                                val usageCount = uiState.usageCounts[feature.featureType] ?: 0
                                val limit = Constants.FREE_MONTHLY_LIMITS[feature.featureType]
                                FeatureCardItem(
                                    feature = feature,
                                    usageCount = usageCount,
                                    limit = if (isPremium || uiState.user?.subscriptionType == User.TRIAL) null else limit,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNavigateToGenerator(feature.featureType) }
                                )
                            }
                            // Fill empty slot if odd number
                            if (rowFeatures.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun FeatureCardItem(
    feature: FeatureCard,
    usageCount: Int,
    limit: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(colors = feature.gradient)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = feature.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                    if (limit != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${limit - usageCount}/$limit left",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpgradeBanner(
    subscriptionType: String,
    trialDaysRemaining: Int,
    onUpgrade: () -> Unit
) {
    val isTrialExpiring = subscriptionType == User.TRIAL && trialDaysRemaining <= 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isTrialExpiring)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isTrialExpiring) Icons.Default.Warning else Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = if (isTrialExpiring)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        isTrialExpiring -> "Trial ending soon"
                        subscriptionType == User.TRIAL -> "You're on Free Trial"
                        else -> "Upgrade to Premium"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = when {
                        subscriptionType == User.TRIAL -> "$trialDaysRemaining days remaining • Unlimited access"
                        else -> "₹999/month • Unlimited + PDF export"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            TextButton(onClick = onUpgrade) {
                Text("Upgrade")
            }
        }
    }
}
