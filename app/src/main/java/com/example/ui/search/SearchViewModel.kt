package com.example.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MovieRepository
import com.example.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val movies: List<Movie>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState
    
    private var searchJob: Job? = null
    
    fun search(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Idle
            return
        }
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            _searchState.value = SearchState.Loading
            try {
                val movies = repository.searchMovies(query)
                if (movies.isEmpty()) {
                    _searchState.value = SearchState.Error("No results found.")
                } else {
                    _searchState.value = SearchState.Success(movies)
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Search failed")
            }
        }
    }
}
