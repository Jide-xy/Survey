package com.example.babajidemustapha.survey.shared.utils

import android.content.Context

object SharedPreferenceHelper {
    private const val PREF_NAME = "user_data"
    fun setGuestUser(context: Context, isGuestUser: Boolean) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(Constants.IS_GUEST, true)
        editor.apply()
    }

    fun isGuestUser(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(Constants.IS_GUEST, false)
    }

    fun setFirstLogin(context: Context) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(Constants.IS_FIRST_LOGIN, false)
        editor.apply()
    }

    fun isfirstLogin(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getBoolean(Constants.IS_FIRST_LOGIN, true)
    }
}