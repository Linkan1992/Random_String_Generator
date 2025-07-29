package com.linkan.randomstringgenerator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkan.randomstringgenerator.domain.model.RandomText
import com.linkan.randomstringgenerator.domain.usecase.GenerateRandomTextUseCase
import com.linkan.randomstringgenerator.util.ResultEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val generateRandomTextUseCase: GenerateRandomTextUseCase)
    : ViewModel() {

    private val _result = MutableLiveData<ResultEvent<RandomText>>()
    val result: LiveData<ResultEvent<RandomText>> = _result

    fun fetchRandomString(length: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _result.postValue(ResultEvent.Loading)
                val random = generateRandomTextUseCase(length)
                _result.postValue(random)
            } catch (e: Exception) {
                // Handle or expose error state
            }
        }
    }
}