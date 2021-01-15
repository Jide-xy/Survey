package com.jide.surveyapp.util

import platform.Foundation.NSUUID

actual object UUID {
    actual fun randomString(): String {
        return NSUUID().UUIDString
    }

}