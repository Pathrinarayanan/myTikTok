package com.example.mytiktok.modal

data class HomeUiState(
    val video : List<String> = emptyList(),
    val loading : Boolean  = false,
    val error : String? = null ,
    val hasMore : Boolean = true
)
