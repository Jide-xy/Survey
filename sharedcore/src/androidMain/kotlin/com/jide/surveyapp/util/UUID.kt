package com.jide.surveyapp.util

import java.util.UUID

actual object UUID {
    actual fun randomString() = UUID.randomUUID().toString()
}