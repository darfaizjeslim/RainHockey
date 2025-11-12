package com.rainhockey.apps.mtavz.data.preferences

import android.content.Context
import android.content.SharedPreferences

class TokenPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "hockey_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_ADDRESS = "address"
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun saveUrl(address: String) {
        prefs.edit().putString(KEY_ADDRESS, address).apply()
    }
    
    fun saveTokenAndUrl(token: String, address: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ADDRESS, address)
            .apply()
    }
    
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun getUrl(): String? {
        return prefs.getString(KEY_ADDRESS, null)
    }
    
    fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }
    
    fun clear() {
        prefs.edit().clear().apply()
    }
}

