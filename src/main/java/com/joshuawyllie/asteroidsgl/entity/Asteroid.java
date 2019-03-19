package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Random;

public class Asteroid extends GLEntity {
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;

    public Asteroid(final float x, final float y, int points) {
        if (points < 3) {
            points = 3;
        } //triangles or more, please. :)
        _x = x;
        _y = y;
        _width = 12;
        _height = _width;
        _velX = Random.between(MIN_VEL * 2, MAX_VEL * 2);
        _velY = Random.between(MIN_VEL * 2, MAX_VEL * 2);
        _angVel = Random.between(MIN_VEL * 1, MAX_VEL * 1);
        final double radius = _width * 0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }
}
