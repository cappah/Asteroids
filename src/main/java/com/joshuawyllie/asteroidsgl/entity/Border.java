package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

public class Border extends GLEntity {
    public Border(final float x, final float y, final float worldWidth, final float worldHeight) {
        super();
        _x = x;
        _y = y;
        setColors(1f, 0f, 0f, 1f); //RED for visibility
        // shortening the variable names to keep the vertex array readable
        final float w = worldWidth;
        final float h = worldHeight;
        // The vertices of the border represent four lines
        final float[] borderVertices = new float[]{
                // A line from point 1 to point 2
                0, 0, 0,
                w, 0, 0,
                // Point 2 to point 3
                w, 0, 0,
                w, h, 0,
                // Point 3 to point 4
                w, h, 0,
                0, h, 0,
                // Point 4 to point 1
                0, h, 0,
                0, 0, 0,
        };
        _mesh = new Mesh(borderVertices, GLES20.GL_LINES);
    }
}
