package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Random;
import com.joshuawyllie.asteroidsgl.util.Utils;

public class Asteroid extends GLEntity {
    public static final int INIT_SIZE = 3;
    private static final float RADIUS_SIZE_3 = 4f;
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;
    private static final double RECOVERY_TIME = 0.5F;
    private static final float ANG_VEL_SCALAR = 0.5f;
    private int size = INIT_SIZE;
    private boolean isRecovering = true;
    private double currentTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;
    private double recoveryTimer = RECOVERY_TIME;

    public Asteroid(final float x, final float y, int size) {
        if (size < 1) {
            size = 1;
        } else {
            this.size = size;
        }
        int points = size + 3;
        if (points < 3) {
            points = 3;
        } //triangles or more, please. :)
        _x = x;
        _y = y;
        _width = RADIUS_SIZE_3 * size;
        _height = RADIUS_SIZE_3 * size;
        _velX = Random.between(MIN_VEL / size, MAX_VEL / size);
        _velY = Random.between(MIN_VEL / size, MAX_VEL / size);
        _angVel = Random.between(MIN_VEL * ANG_VEL_SCALAR / size, MAX_VEL * ANG_VEL_SCALAR / size);
        final double radius = _width * 0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        mesh = new Mesh(verts, GLES20.GL_LINES);
        mesh.setWidthHeight(_width, _height);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        final double newTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        if (isRecovering) {
            recoveryTimer -= frameTime;
            if (recoveryTimer < 0) {
                recoveryTimer = RECOVERY_TIME;
                isRecovering = false;
            }
        }
    }

    public float getX() {
        return _x;
    }

    public float getY() {
        return _y;
    }

    public int getSize() {
        return size;
    }

    public boolean isRecovering() {
        return isRecovering;
    }
}
