package com.example.data.repository

import com.example.data.remote.TMDBApi
import com.example.data.remote.MovieDto
import com.example.domain.model.Movie

class MovieRepository(private val api: TMDBApi, private val apiKey: String) {
    
    private fun mapDtoToMovie(dto: MovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.title ?: "Unknown",
            overview = dto.overview ?: "",
            posterUrl = if (dto.poster_path != null) "https://image.tmdb.org/t/p/w500${dto.poster_path}" else "",
            backdropUrl = if (dto.backdrop_path != null) "https://image.tmdb.org/t/p/w780${dto.backdrop_path}" else "",
            releaseDate = dto.release_date ?: ""
        )
    }

    suspend fun getPopularMovies(page: Int = 1): List<Movie> {
        if (apiKey.isEmpty() || apiKey.startsWith("MY_")) return emptyList()
        val response = api.getPopularMovies(apiKey, page = page)
        return response.results.map { mapDtoToMovie(it) }
    }

    suspend fun getTrendingMovies(page: Int = 1): List<Movie> {
        if (apiKey.isEmpty() || apiKey.startsWith("MY_")) return emptyList()
        val response = api.getTrendingMovies(apiKey, page = page)
        return response.results.map { mapDtoToMovie(it) }
    }

    suspend fun getTopRatedMovies(page: Int = 1): List<Movie> {
        if (apiKey.isEmpty() || apiKey.startsWith("MY_")) return emptyList()
        val response = api.getTopRatedMovies(apiKey, page = page)
        return response.results.map { mapDtoToMovie(it) }
    }

    suspend fun getUpcomingMovies(page: Int = 1): List<Movie> {
        if (apiKey.isEmpty() || apiKey.startsWith("MY_")) return emptyList()
        val response = api.getUpcomingMovies(apiKey, page = page)
        return response.results.map { mapDtoToMovie(it) }
    }

    suspend fun searchMovies(query: String, page: Int = 1): List<Movie> {
        if (apiKey.isEmpty() || apiKey.startsWith("MY_") || query.isBlank()) return emptyList()
        val response = api.searchMovies(query, apiKey, page = page)
        return response.results.map { mapDtoToMovie(it) }
    }
    
    suspend fun getMovieDetails(movieId: Int): Movie? {
        if (apiKey.isEmpty() || apiKey.startsWith("MY_")) return null
        return try {
            val dto = api.getMovieDetails(movieId, apiKey)
            mapDtoToMovie(dto)
        } catch (e: Exception) {
            null
        }
    }
}
