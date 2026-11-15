package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FaqItem

@Composable
fun FaqSection(
    language: String,
    faqs: List<FaqItem>,
    modifier: Modifier = Modifier
) {
    var expandedFaqId by remember { mutableStateOf<String?>(faqs.firstOrNull()?.id) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Zote", "Mfumo wa Fahamu", "Malezi ya Kidijitali", "Lishe ya Mtoto", "Malipo & Huduma ya Pro", "Mawasiliano")

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth().testTag("faq_section")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (language == "sw") "Maswali Yanayoulizwa Mara kwa Mara (FAQs)" else "Frequently Asked Questions (FAQs)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (language == "sw") "Majibu ya haraka ya wataalamu wa malezi" else "Expert answers on child neurology & digital habits",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = (cat == "Zote" && selectedCategory == null) || selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (cat == "Zote") null else cat
                        },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val filteredFaqs = faqs.filter {
                selectedCategory == null || it.category.contains(selectedCategory!!, ignoreCase = true)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredFaqs.forEach { faq ->
                    val isExpanded = expandedFaqId == faq.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isExpanded)
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedFaqId = if (isExpanded) null else faq.id
                            }
                            .testTag("faq_item_${faq.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (language == "sw") faq.questionSwahili else faq.questionEnglish,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = if (language == "sw") faq.answerSwahili else faq.answerEnglish,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
