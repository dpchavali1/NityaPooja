package com.nityapooja.shared.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nityapooja.shared.data.local.entity.PuranaQuizEntity
import com.nityapooja.shared.data.repository.DevotionalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizQuestionState(
    val question: PuranaQuizEntity,
    val selectedOption: Int? = null, // 1-4, null = not answered
    val revealed: Boolean = false,
)

data class PuranaQuizUiState(
    val questions: List<QuizQuestionState> = emptyList(),
    val isLoading: Boolean = true,
    val score: Int = 0,
    val allAnswered: Boolean = false,
)

class PuranaQuizViewModel(
    private val repository: DevotionalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuranaQuizUiState())
    val uiState: StateFlow<PuranaQuizUiState> = _uiState.asStateFlow()

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        _uiState.value = PuranaQuizUiState(isLoading = true)
        viewModelScope.launch {
            repository.getRandomQuizzes(5).collect { questions ->
                _uiState.value = PuranaQuizUiState(
                    questions = questions.map { QuizQuestionState(it) },
                    isLoading = false,
                )
            }
        }
    }

    fun selectAnswer(questionIndex: Int, option: Int) {
        val current = _uiState.value
        if (questionIndex >= current.questions.size) return
        val q = current.questions[questionIndex]
        if (q.revealed) return // already answered

        val updated = current.questions.toMutableList()
        updated[questionIndex] = q.copy(selectedOption = option, revealed = true)

        val score = updated.count { it.revealed && it.selectedOption == it.question.correctOption }
        val allAnswered = updated.all { it.revealed }

        _uiState.value = current.copy(questions = updated, score = score, allAnswered = allAnswered)
    }
}
