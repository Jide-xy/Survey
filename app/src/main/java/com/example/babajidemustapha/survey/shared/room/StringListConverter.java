package com.example.babajidemustapha.survey.shared.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class StringListConverter {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        if (value != null) {
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            return new Gson().fromJson(value, listType);
        }
        return null;
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        return list == null || list.isEmpty() ? null : new Gson().toJson(list);
    }
}
