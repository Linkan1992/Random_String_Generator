package com.linkan.randomstringgenerator.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.linkan.randomstringgenerator.domain.model.RandomText
import com.linkan.randomstringgenerator.domain.repository.RandomTextRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class RandomTextProviderDataSource @Inject constructor (
    @ApplicationContext private val context: Context
) : RandomTextRepository {

    private val uri = Uri.parse("content://com.iav.contestdataprovider/text")

    override suspend fun getRandomText(length: Int): RandomText {
        val bundle = Bundle().apply {
            putInt("data", length)
            putInt(ContentResolver.QUERY_ARG_LIMIT, 1)
        }

        val cursor = context.contentResolver.query(uri, null, bundle, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val jsonStr = it.getString(it.getColumnIndexOrThrow("data"))
                val json = JSONObject(jsonStr).getJSONObject("randomText")

                return RandomText(
                    value = json.getString("value"),
                    length = json.getInt("length"),
                    created = json.getString("created")
                )
            } else {
                throw Exception("No data received from provider")
            }
        }
        throw Exception("Failed to query content provider")
    }
}