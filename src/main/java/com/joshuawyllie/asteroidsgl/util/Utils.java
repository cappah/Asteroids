package com.joshuawyllie.asteroidsgl.util;

import android.content.res.Resources;

public abstract class Utils {
    public static float wrap(float value, final float min, final float max) {
        if (value < min) {
            value = max;
        } else if (value > max) {
            value = min;
        }
        return value;
    }

    public static float clamp(float value, final float min, final float max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }

    public static int pxToDp(final int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
