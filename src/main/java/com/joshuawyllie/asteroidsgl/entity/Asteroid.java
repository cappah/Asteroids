package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Random;
import com.joshuawyllie.asteroidsgl.util.Utils;

public class Asteroid extends GLEntity {
    private static final float MAX_VEL = 8f;
    private static final float MIN_VEL = -8f;

    public Asteroid(final float x, final float y, int points) {
        if (points < 3) {
            points = 3;
        } //triangles or more, please. :)
        _x = x;
        _y = y;
        _velX = Random.between(MIN_VEL, MAX_VEL);
        _velY = Random.between(MIN_VEL, MAX_VEL);
        final double radius = 6.0;
        final int numVerts = points * 2; //we render lines, and each line requires 2 points
        final float[] verts = new float[numVerts * Mesh.COORDS_PER_VERTEX];

        double step = 2.0 * Math.PI / points;
        int i = 0, point = 0;
        while (point < points) { //generate verts on circle, 2 per point
            double theta = point * step;
            verts[i++] = (float) (Math.cos(theta) * radius); //X
            verts[i++] = (float) (Math.sin(theta) * radius); //Y
            verts[i++] = 0f;                                //Z
            point++;
            theta = point * step;
            verts[i++] = (float) (Math.cos(theta) * radius); //X
            verts[i++] = (float) (Math.sin(theta) * radius); //Y
            verts[i++] = 0f;                                //Z
        }
        _mesh = new Mesh(verts, GLES20.GL_LINES);
    }
}
