package com.joshuawyllie.asteroidsgl.util;

import android.content.res.Resources;
import android.util.Log;

public abstract class Utils {
    public static final double CIRCLE_DEG = 360;
    public static final double TO_DEG = 180.0 / Math.PI;
    public static final double TO_RAD = Math.PI / 180.0;
    public static final long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static final float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static final long SECOND_IN_NANOSECONDS = 1000000000;
    public static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    public static final float RGB_TO_FLOAT = 1f / 255f;

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

    public static void expect(final boolean condition, final String tag) {
        Utils.expect(condition, tag, "Expectation was broken.");
    }

    public static void expect(final boolean condition, final String tag, final String message) {
        if (!condition) {
            Log.e(tag, message);
        }
    }

    public static void require(final boolean condition) {
        Utils.require(condition, "Assertion failed!");
    }

    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
