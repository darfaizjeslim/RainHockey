package com.rainhockey.apps.mtavz.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ServerService {
    
    suspend fun fetchServerData(link: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val connection = URL(link).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                connection.disconnect()
                
                Result.success(response.toString())
            } else {
                connection.disconnect()
                Result.failure(Exception("HTTP error code: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun parseResponse(response: String): Pair<String?, String?> {
        if (response.contains("#")) {
            val parts = response.split("#", limit = 2)
            return Pair(parts[0], parts.getOrNull(1))
        }
        return Pair(null, null)
    }
}

