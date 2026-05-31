package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MovieRepository
import com.example.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val trendingMovies: List<Movie>,
        val popularMovies: List<Movie>,
        val topRatedMovies: List<Movie>,
        val upcomingMovies: List<Movie>
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState

    init {
        loadData()
    }

    fun loadData() {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            try {
                val trendingDef = async { repository.getTrendingMovies() }
                val popularDef = async { repository.getPopularMovies() }
                val topRatedDef = async { repository.getTopRatedMovies() }
                val upcomingDef = async { repository.getUpcomingMovies() }

                val trending = trendingDef.await()
                val popular = popularDef.await()
                val topRated = topRatedDef.await()
                val upcoming = upcomingDef.await()

                if (trending.isEmpty() && popular.isEmpty() && topRated.isEmpty() && upcoming.isEmpty()) {
                    _homeState.value = HomeState.Error("No API Key or no movies found. Please configure TMDB_API_KEY in Secrets.")
                } else {
                    _homeState.value = HomeState.Success(
                        trendingMovies = trending,
                        popularMovies = popular,
                        topRatedMovies = topRated,
                        upcomingMovies = upcoming
                    )
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "Failed to load movies")
            }
        }
    }
}
