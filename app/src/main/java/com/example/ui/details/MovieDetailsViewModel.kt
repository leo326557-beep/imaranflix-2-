package com.example.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MovieRepository
import com.example.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MovieDetailsState {
    object Loading : MovieDetailsState()
    data class Success(val movie: Movie) : MovieDetailsState()
    data class Error(val message: String) : MovieDetailsState()
}

class MovieDetailsViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _state = MutableStateFlow<MovieDetailsState>(MovieDetailsState.Loading)
    val state: StateFlow<MovieDetailsState> = _state

    fun loadMovie(id: Int) {
        _state.value = MovieDetailsState.Loading
        viewModelScope.launch {
            val movie = repository.getMovieDetails(id)
            if (movie != null) {
                _state.value = MovieDetailsState.Success(movie)
            } else {
                _state.value = MovieDetailsState.Error("Movie not found")
            }
        }
    }
}
