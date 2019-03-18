package com.joshuawyllie.asteroidsgl.entity;

import android.graphics.Color;
import android.opengl.GLES20;

public class Star extends GLEntity {
    private static Mesh m = null; //Q&D pool

    public Star(final float x, final float y) {
        super();
        _x = x;
        _y = y;
        _color[0] = Color.red(Color.YELLOW) / 255f;
        _color[1] = Color.green(Color.YELLOW) / 255f;
        _color[2] = Color.blue(Color.YELLOW) / 255f;
        _color[3] = 1f;
        if (m == null) {
            final float[] vertices = {0, 0, 0};
            m = new Mesh(vertices, GLES20.GL_POINTS);
        }
        _mesh = m; //all Stars use the exact same Mesh instance.
    }
}