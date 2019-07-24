package com.example.babajidemustapha.survey.shared.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListConverter {
    @TypeConverter
    public static List<String> fromString(String value) {
        if (value != null) {
            return new ArrayList<>(Arrays.asList(new Gson().fromJson(value, String[].class)));
        }
        return new ArrayList<>();
    }

    @TypeConverter
    public static String fromArrayList(List<String> list) {
        return list == null || list.isEmpty() ? null : new Gson().toJson(list);
    }
}
