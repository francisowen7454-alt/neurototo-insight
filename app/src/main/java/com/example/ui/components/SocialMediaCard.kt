package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GrowthTeal

data class SocialChannel(
    val name: String,
    val handle: String,
    val url: String,
    val brandColor: Color,
    val subtitleSwahili: String,
    val subtitleEnglish: String
)

val OfficialSocialChannels = listOf(
    SocialChannel(
        name = "Facebook",
        handle = "NeuroToto Insight",
        url = "https://www.facebook.com/profile.php?id=61576289362276",
        brandColor = Color(0xFF1877F2),
        subtitleSwahili = "Ushauri wa malezi, makala na jumuiya ya wazazi",
        subtitleEnglish = "Parenting advice, articles & community"
    ),
    SocialChannel(
        name = "TikTok",
        handle = "@neurototo.insight",
        url = "https://www.tiktok.com/@neurototo.insight",
        brandColor = Color(0xFF000000),
        subtitleSwahili = "Video fupi za ubongo, lishe na mbinu za watoto",
        subtitleEnglish = "Quick brain development & pediatric tips"
    ),
    SocialChannel(
        name = "YouTube",
        handle = "@NeurototoInsight",
        url = "https://www.youtube.com/@NeurototoInsight",
        brandColor = Color(0xFFFF0000),
        subtitleSwahili = "Masomo ya video ya kina na ushauri wa madaktari",
        subtitleEnglish = "Deep-dive video masterclasses by doctors"
    )
)

@Composable
fun SocialMediaSection(
    language: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("social_media_section")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == "sw") "Tufuate Kwenye Mitandao ya Kijamii" else "Follow NeuroToto Insight",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (language == "sw") "Video mpya na mafunzo ya kila siku" else "Official channels for daily insights",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OfficialSocialChannels.forEach { channel ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    if (com.example.util.SecurityUtils.isSafeWebUrl(channel.url)) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(channel.url)).apply {
                                            addCategory(Intent.CATEGORY_BROWSABLE)
                                        }
                                        context.startActivity(intent)
                                    }
                                } catch (_: Exception) {
                                    // Fallback if no intent handler
                                }
                            }
                            .testTag("social_btn_${channel.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(channel.brandColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = channel.name.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = channel.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = channel.handle,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = channel.brandColor,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    Text(
                                        text = if (language == "sw") channel.subtitleSwahili else channel.subtitleEnglish,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
