package com.linkan.randomstringgenerator.domain.usecase

import com.linkan.randomstringgenerator.domain.model.RandomText
import com.linkan.randomstringgenerator.domain.repository.RandomTextRepository
import com.linkan.randomstringgenerator.util.ResultEvent
import javax.inject.Inject

class GenerateRandomTextUseCase @Inject constructor(
    private val repository: RandomTextRepository
) {
    suspend operator fun invoke(length: Int): ResultEvent<RandomText> {
        return try {
            val result = repository.getRandomText(length)
            ResultEvent.Success(result)
        } catch (e: Exception) {
            ResultEvent.Error(e)
        }
    }
}