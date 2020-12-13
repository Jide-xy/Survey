package com.example.babajidemustapha.survey.shared.utils

import android.content.res.Resources
import android.util.TypedValue

object Utils {
    fun pxToDp(resources: Resources, px: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px.toFloat(), resources.displayMetrics).toInt()
    }
}