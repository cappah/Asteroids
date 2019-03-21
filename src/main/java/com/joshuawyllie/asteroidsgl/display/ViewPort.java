package com.joshuawyllie.asteroidsgl.display;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import com.joshuawyllie.asteroidsgl.event.Event;

public class ViewPort {
    public static final float WORLD_WIDTH = 160f; //all dimensions are in meters
    public static final float WORLD_HEIGHT = 90f;
    private final Context context;
    private final PointF lookAt = new PointF(0f, 0f);

    private float[] viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera
    private final int offset = 0;
    private float left = 0f;
    private float right;
    private float bottom;
    private float top = 0f;
    private final float near = 0f;
    private final float far = 1f;

    /**
     * Defaults to letter boxing the screen
     * @param context
     */
    public ViewPort(final Context context) {
        this.context = context;
        this.right = WORLD_WIDTH;
        this.bottom = WORLD_HEIGHT;
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);
    }

    public void onEvent(Event event) {
        switch (event.getType()) {
            case SURFACE_CHANGED:
                break;
        }
    }

    public void onSurfaceCreated() {
    }

    public void onSurfaceChanged(int width, int height) {
        float screenRatio = (float) width / (float) height;
        float worldRatio = WORLD_WIDTH / WORLD_HEIGHT;
        if (screenRatio > worldRatio) {
            this.right = WORLD_HEIGHT * screenRatio;
            this.bottom = WORLD_HEIGHT;
            float xOffset = (this.right - WORLD_WIDTH) / 2f;
            this.left -= xOffset;
            this.right -= xOffset;
        } else {
            this.right = WORLD_WIDTH;
            this.bottom = WORLD_WIDTH / screenRatio;
            float yOffset = (this.bottom - WORLD_HEIGHT) / 2f;
            this.bottom += yOffset;
            this.top += yOffset;
        }
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);
    }

    public float[] getViewportMatrix() {
        return this.viewportMatrix;
    }
}
