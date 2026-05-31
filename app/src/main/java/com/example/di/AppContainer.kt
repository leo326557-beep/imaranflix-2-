package com.example.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.remote.TMDBApi
import com.example.data.repository.AuthRepository
import com.example.data.repository.MovieRepository
import com.example.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.BuildConfig

class AppContainer(private val context: Context) {
    val auth by lazy { FirebaseAuth.getInstance() }
    val firestore by lazy { FirebaseFirestore.getInstance() }
    val storage by lazy { FirebaseStorage.getInstance() }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        
    val tmdbApi = retrofit.create(TMDBApi::class.java)

    val tmdbKey = BuildConfig.TMDB_API_KEY 

    val authRepository by lazy { AuthRepository(auth) }
    val userRepository by lazy { UserRepository(firestore, storage) }
    val movieRepository by lazy { MovieRepository(tmdbApi, tmdbKey) }
}

object ViewModelFactoryHelper {
    fun provideFactory(appContainer: AppContainer): ViewModelProvider.Factory = 
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(com.example.ui.auth.AuthViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return com.example.ui.auth.AuthViewModel(appContainer.authRepository) as T
                }
                if (modelClass.isAssignableFrom(com.example.ui.home.HomeViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return com.example.ui.home.HomeViewModel(appContainer.movieRepository) as T
                }
                if (modelClass.isAssignableFrom(com.example.ui.search.SearchViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return com.example.ui.search.SearchViewModel(appContainer.movieRepository) as T
                }
                if (modelClass.isAssignableFrom(com.example.ui.details.MovieDetailsViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return com.example.ui.details.MovieDetailsViewModel(appContainer.movieRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class \$modelClass")
            }
        }
}

