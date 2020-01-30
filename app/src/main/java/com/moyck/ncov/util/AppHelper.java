package com.moyck.ncov.util;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Field;

public class AppHelper {

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


}
