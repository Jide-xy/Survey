package com.jide.surveyapp.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object DateUtil {
    actual val timeInMilliseconds: Long
        get() = NSDate().timeIntervalSince1970.toLong() * 1000

}