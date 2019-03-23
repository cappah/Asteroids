package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Utils;

public class Flame extends GLEntity {
    private static final float INIT_WIDTH = 4f;
    private static final float INIT_HEIGHT = 6f;
    private static final float ENTITY_OFFSET = 0.75f;

    public Flame(final float x, final float y) {
        super();
        _x = x;
        _y = y;
        _width = INIT_WIDTH;
        _height = INIT_HEIGHT;
        float vertices[] = { // in counterclockwise order:
                0.5f, 0.5f, 0.0f,
                0.7f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,

                0.5f, 0.5f, 0.0f,
                0.0f, -0.8f, 0.0f,
                -0.5f, 0.5f, 0.0f,

                0.5f, 0.5f, 0.0f,
                -0.7f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,

        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
    }

   public void followEntity(GLEntity entity) {
        _x = entity._x + (float) Math.sin(_rotation * Utils.TO_RAD) * entity._height * ENTITY_OFFSET;
        _y = entity._y - (float) Math.cos(_rotation * Utils.TO_RAD) * entity._height * ENTITY_OFFSET;
       _rotation = entity._rotation + (float) Utils.CIRCLE_DEG * 0.5f;
   }

    public void setSize(final float width, final float height) {
        this._width = width;
        this._height = height;
    }
}
