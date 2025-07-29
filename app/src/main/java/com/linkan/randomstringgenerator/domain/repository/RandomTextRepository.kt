package com.linkan.randomstringgenerator.domain.repository

import com.linkan.randomstringgenerator.domain.model.RandomText

interface RandomTextRepository {
    suspend fun getRandomText(length: Int): RandomText
}