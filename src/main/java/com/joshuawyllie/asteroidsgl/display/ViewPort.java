package com.joshuawyllie.asteroidsgl.display;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.Matrix;

import com.joshuawyllie.asteroidsgl.entity.GLEntity;
import com.joshuawyllie.asteroidsgl.event.Event;

public class ViewPort {
    public enum ViewPortMode { LETTER_BOX, FILL }
    public static final float WORLD_WIDTH = 160f; //all dimensions are in meters
    public static float WORLD_HEIGHT = 90f;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private final Context context;
    private PointF lookAt = new PointF(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f);
    private ViewPortMode mode;

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
    public ViewPort(final Context context, final ViewPortMode mode) {
        this.context = context;
        this.mode = mode;
        this.right = WORLD_WIDTH;
        this.bottom = WORLD_HEIGHT;
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);
    }

    public void lookAt(PointF point) {
        this.lookAt = point;
        updateViewPortLetterBox();
    }

    public void lookAt(GLEntity entity) {
        this.lookAt = entity.getPos();
        updateViewPortLetterBox();
    }


    public void onEvent(Event event) {
        switch (event.getType()) {
            case SURFACE_CHANGED:
                break;
        }
    }

    public void onSurfaceCreated(int widthPixels, int heightPixels) {
        this.widthPixels = widthPixels;
        this.heightPixels = heightPixels;
        updateViewport();
    }

    public void onSurfaceChanged(int widthPixels, int heightPixels) {
        this.widthPixels = widthPixels;
        this.heightPixels = heightPixels;
        updateViewport();
    }

    private void updateViewport() {
        switch (mode) {
            case FILL:
                updateViewPortFill();
                break;
            case LETTER_BOX:
                updateViewPortLetterBox();
                break;
        }
    }

    private void updateViewPortLetterBox() {
        final float screenRatio = (float) widthPixels / (float) heightPixels;
        final float worldRatio = WORLD_WIDTH / WORLD_HEIGHT;
        float xOffset = 0;
        float yOffset = 0;
        if (screenRatio > worldRatio) {
            this.right = WORLD_HEIGHT * screenRatio;
            this.bottom = WORLD_HEIGHT;
            xOffset = lookAt.x - WORLD_WIDTH * 0.5f;
            xOffset += (this.right - WORLD_WIDTH) * 0.5f;
        } else {
            this.right = WORLD_WIDTH;
            this.bottom = WORLD_WIDTH / screenRatio;
            yOffset = lookAt.y - WORLD_HEIGHT * 0.5f;
            yOffset += (this.bottom - WORLD_HEIGHT) * 0.5f;
        }
        Matrix.orthoM(viewportMatrix, offset, left - xOffset, right - xOffset, bottom + yOffset, top + yOffset, near, far);
    }

    private void updateViewPortFill() {
        final float screenRatio = (float) widthPixels / (float) heightPixels;

        WORLD_HEIGHT = WORLD_WIDTH / screenRatio;
        this.bottom = WORLD_HEIGHT;
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);

    }

    public float[] getViewportMatrix() {
        return this.viewportMatrix;
    }

    public PointF worldCenter() {
        return new PointF(WORLD_WIDTH * 0.5f, WORLD_HEIGHT * 0.5f);
    }

    public float viewPortWidthMeters() {
        return right - left;
    }

    public float viewPortHeightMeters() {
        return top - bottom;
    }
}
