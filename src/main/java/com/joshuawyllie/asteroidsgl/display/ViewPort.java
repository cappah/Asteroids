package com.joshuawyllie.asteroidsgl.display;

import android.graphics.PointF;
import android.view.SurfaceHolder;

import com.joshuawyllie.asteroidsgl.event.Event;
import com.joshuawyllie.asteroidsgl.event.EventType;

public class ViewPort implements SurfaceHolder.Callback {
    private int screenWidth;
    private int screenHeight;
    private final float metersToShowX;
    private final float metersToShowY;
    private final PointF lookAt = new PointF(0f, 0f);

    public ViewPort(final int screenWidth, final int screenHeight, final float metersToShowX, final float metersToShowY, SurfaceHolder surfaceHolder) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.metersToShowX = metersToShowX;
        this.metersToShowY = metersToShowY;
        surfaceHolder.addCallback(this);
    }

    public void onEvent(Event event) {
        switch (event.getType()) {
            case SURFACE_CHANGED:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
