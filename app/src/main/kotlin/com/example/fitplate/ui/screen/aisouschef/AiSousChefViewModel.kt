package com.example.fitplate.ui.screen.aisouschef

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitplate.BuildConfig
import com.example.fitplate.data.network.GroqApi
import com.example.fitplate.data.network.model.GroqMessage
import com.example.fitplate.data.network.model.GroqRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val showRecipe: Boolean = false
)

data class AiSousChefUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class AiSousChefViewModel @Inject constructor(
    private val groqApi: GroqApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiSousChefUiState())
    val uiState: StateFlow<AiSousChefUiState> = _uiState.asStateFlow()

    private val chatHistory = mutableListOf<GroqMessage>(
        GroqMessage(role = "system", content = "You are an AI Sous Chef for the Fit Plate app. You help users find healthy recipes based on ingredients they have and their dietary preferences. You should be encouraging, professional, and focus on healthy, macro-balanced meals.")
    )

    init {
        _uiState.value = AiSousChefUiState(
            messages = listOf(
                ChatMessage("Hello! I'm your AI Sous Chef. What ingredients do you have in your fridge?", isUser = false)
            )
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text, isUser = true)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true
        )

        chatHistory.add(GroqMessage(role = "user", content = text))

        viewModelScope.launch {
            try {
                val response = groqApi.getChatCompletion(
                    authHeader = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = GroqRequest(messages = chatHistory)
                )

                if (response.isSuccessful) {
                    val aiMessageText = response.body()?.choices?.firstOrNull()?.message?.content ?: "I'm sorry, I couldn't process that."
                    chatHistory.add(GroqMessage(role = "assistant", content = aiMessageText))
                    
                    val showRecipe = aiMessageText.contains("recipe", ignoreCase = true) || 
                                     aiMessageText.contains("here is", ignoreCase = true)

                    val aiMessage = ChatMessage(aiMessageText, isUser = false, showRecipe = showRecipe)
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isLoading = false
                    )
                } else {
                    throw Exception("API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AiSousChefViewModel", "Error: ${e.message}", e)
                val errorMessage = if (e.message?.contains("429") == true) {
                    "Free tier limit reached. Please wait a minute."
                } else {
                    "Error: ${e.message ?: "Please check your internet"}"
                }
                
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + ChatMessage(errorMessage, isUser = false),
                    isLoading = false
                )
            }
        }
    }
}
