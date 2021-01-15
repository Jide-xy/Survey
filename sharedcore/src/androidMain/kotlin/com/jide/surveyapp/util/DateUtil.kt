package com.jide.surveyapp.util

actual object DateUtil {
    actual val timeInMilliseconds: Long
        get() = System.currentTimeMillis()
}