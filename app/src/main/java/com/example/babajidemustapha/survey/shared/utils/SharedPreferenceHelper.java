package com.example.babajidemustapha.survey.shared.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
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
}
