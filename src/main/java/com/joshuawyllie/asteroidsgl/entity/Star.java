package com.joshuawyllie.asteroidsgl.entity;

import android.graphics.Color;
import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Utils;

public class Star extends GLEntity {
    private static Mesh m = null; //Q&D pool

    public Star(final float x, final float y) {
        super(x, y);
        _color[0] = Color.red(Color.YELLOW) * Utils.RGB_TO_FLOAT;
        _color[1] = Color.green(Color.YELLOW) * Utils.RGB_TO_FLOAT;
        _color[2] = Color.blue(Color.YELLOW) * Utils.RGB_TO_FLOAT;
        _color[3] = 1f;
        if (m == null) {
            final float[] vertices = {0, 0, 0};
            m = new Mesh(vertices, GLES20.GL_POINTS);
        }
        mesh = m; //all Stars use the exact same Mesh instance.
    }
}