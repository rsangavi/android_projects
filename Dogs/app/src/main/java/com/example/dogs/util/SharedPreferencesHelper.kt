package com.example.dogs.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SharedPreferencesHelper {

    companion object {
        @Volatile
        private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        private var prefs: SharedPreferences? = null
        private const val PREF_TIME = "Pref time"

        operator fun invoke(context: Context): SharedPreferencesHelper = instance ?: synchronized(LOCK) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }

    fun saveUpdatedTime(time: Long) {
        prefs?.edit(commit = true) { putLong(PREF_TIME, time)}

    }

    fun getUpdatedTime() = prefs?.getLong(PREF_TIME, 0)

    fun getCacheSettingsTime() = prefs?.getString("pref_cache_duration", "")

}