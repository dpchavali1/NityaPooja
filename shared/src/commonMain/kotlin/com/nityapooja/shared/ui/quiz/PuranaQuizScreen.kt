package com.nityapooja.shared.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nityapooja.shared.data.local.entity.PuranaQuizEntity
import com.nityapooja.shared.ui.components.FontSizeViewModel
import com.nityapooja.shared.ui.theme.TempleGold
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuranaQuizScreen(
    onBack: () -> Unit,
    viewModel: PuranaQuizViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val fontSizeViewModel: FontSizeViewModel = koinViewModel()
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    val fontScale = fontSize / 16f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("పురాణాల క్విజ్", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Puranas Quiz", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.allAnswered) {
                        IconButton(onClick = { viewModel.loadQuiz() }) {
                            Icon(Icons.Default.Refresh, "New Quiz", tint = TempleGold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TempleGold)
                }
            }
            uiState.questions.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Quiz questions not available yet.\nPlease restart the app.", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Score header (shown when all answered)
                    if (uiState.allAnswered) {
                        item {
                            ScoreCard(score = uiState.score, total = uiState.questions.size, fontScale = fontScale) {
                                viewModel.loadQuiz()
                            }
                        }
                    }

                    itemsIndexed(uiState.questions) { index, questionState ->
                        QuizQuestionCard(
                            index = index,
                            state = questionState,
                            fontScale = fontScale,
                            onOptionSelected = { option -> viewModel.selectAnswer(index, option) },
                        )
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(score: Int, total: Int, fontScale: Float = 1f, onNewQuiz: () -> Unit) {
    val percentage = if (total > 0) (score * 100) / total else 0
    val color = when {
        percentage >= 80 -> Color(0xFF2E7D32)
        percentage >= 60 -> Color(0xFFF57F17)
        else -> MaterialTheme.colorScheme.error
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Default.CheckCircle, "Score", tint = color, modifier = Modifier.size(40.dp))
            Text("$score / $total", style = MaterialTheme.typography.displaySmall.copy(fontSize = (36 * fontScale).sp), fontWeight = FontWeight.Bold, color = color)
            Text(
                when {
                    percentage == 100 -> "అద్భుతం! Perfect score! 🙏"
                    percentage >= 80 -> "శభాష్! Excellent!"
                    percentage >= 60 -> "బాగుంది! Good job!"
                    else -> "మళ్ళీ ప్రయత్నించండి! Try again!"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp),
                color = color,
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onNewQuiz,
                colors = ButtonDefaults.buttonColors(containerColor = TempleGold),
            ) {
                Icon(Icons.Default.Refresh, "Refresh", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("క్రొత్త క్విజ్ / New Quiz")
            }
        }
    }
}

@Composable
private fun QuizQuestionCard(
    index: Int,
    state: QuizQuestionState,
    fontScale: Float = 1f,
    onOptionSelected: (Int) -> Unit,
) {
    val q = state.question
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Question number + puranam source
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Q${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = TempleGold,
                )
                Text(
                    q.puranamTelugu,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Question text
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(q.questionTelugu, style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * fontScale).sp, lineHeight = (24 * fontScale).sp), fontWeight = FontWeight.Medium)
                Text(q.question, style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * fontScale).sp, lineHeight = (22 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider(thickness = 0.5.dp)

            // Options
            val options = listOf(
                Triple(1, q.option1, q.option1Telugu),
                Triple(2, q.option2, q.option2Telugu),
                Triple(3, q.option3, q.option3Telugu),
                Triple(4, q.option4, q.option4Telugu),
            )
            options.forEach { (num, eng, tel) ->
                OptionButton(
                    number = num,
                    english = eng,
                    telugu = tel,
                    isSelected = state.selectedOption == num,
                    isCorrect = num == q.correctOption,
                    revealed = state.revealed,
                    fontScale = fontScale,
                    onClick = { onOptionSelected(num) },
                )
            }

            // Source info after reveal
            if (state.revealed) {
                HorizontalDivider(thickness = 0.5.dp)
                Text(
                    "📖 ${q.puranam}${if (q.chapter.isNotBlank()) " • ${q.chapter}" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OptionButton(
    number: Int,
    english: String,
    telugu: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    revealed: Boolean,
    fontScale: Float = 1f,
    onClick: () -> Unit,
) {
    val containerColor = when {
        !revealed -> if (isSelected) TempleGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        isCorrect -> Color(0xFF2E7D32).copy(alpha = 0.15f)
        isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        !revealed -> if (isSelected) TempleGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        isCorrect -> Color(0xFF2E7D32)
        isSelected && !isCorrect -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        onClick = { if (!revealed) onClick() },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Option letter badge
            val labelColor = when {
                !revealed -> if (isSelected) TempleGold else MaterialTheme.colorScheme.onSurfaceVariant
                isCorrect -> Color(0xFF2E7D32)
                isSelected -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                ('A' + number - 1).toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = labelColor,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(telugu, style = MaterialTheme.typography.bodyMedium.copy(fontSize = (15 * fontScale).sp, lineHeight = (22 * fontScale).sp), fontWeight = FontWeight.Medium)
                if (english != telugu) {
                    Text(english, style = MaterialTheme.typography.labelSmall.copy(fontSize = (13 * fontScale).sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (revealed && isCorrect) {
                Icon(Icons.Default.CheckCircle, "Correct", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
            }
        }
    }
}
