package com.example.babajidemustapha.survey.shared.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
    public static int pxToDp(Resources resources, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, resources.getDisplayMetrics());
    }
}
