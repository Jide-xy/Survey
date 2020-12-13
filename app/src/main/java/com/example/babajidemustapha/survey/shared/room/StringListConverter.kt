package com.example.babajidemustapha.survey.shared.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

object StringListConverter {
    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return if (value != null) {
            ArrayList(Arrays.asList(*Gson().fromJson(value, Array<String>::class.java)))
        } else ArrayList()
    }

    @kotlin.jvm.JvmStatic
    @TypeConverter
    fun fromArrayList(list: List<String?>?): String? {
        return if (list == null || list.isEmpty()) null else Gson().toJson(list)
    }
}