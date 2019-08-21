package com.example.babajidemustapha.survey.shared.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.example.babajidemustapha.survey.shared.utils.Constants.IS_FIRST_LOGIN;
import static com.example.babajidemustapha.survey.shared.utils.Constants.IS_GUEST;

public class SharedPreferenceHelper {

    private static final String PREF_NAME = "user_data";

    public static void setGuestUser(Context context, boolean isGuestUser) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(IS_GUEST, true);
        editor.apply();
    }

    public static boolean isGuestUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getBoolean(IS_GUEST, false);
    }

    public static void setFirstLogin(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(IS_FIRST_LOGIN, false);
        editor.apply();
    }

    public static boolean isfirstLogin(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getBoolean(IS_FIRST_LOGIN, true);
    }
}
