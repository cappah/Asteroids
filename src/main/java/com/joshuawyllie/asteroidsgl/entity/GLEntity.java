package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.Matrix;

import com.joshuawyllie.asteroidsgl.GLManager;
import com.joshuawyllie.asteroidsgl.Game;

import java.util.Objects;

public class GLEntity {
    public static final float[] modelMatrix = new float[4*4];
    public static final float[] viewportModelMatrix = new float[4*4];
    public static final float[] rotationViewportModelMatrix = new float[4*4];
    public static Game _game = null; //shared ref, managed by the Game-class!
    Mesh _mesh = null;
    float _color[] = { 1.0f, 1.0f, 1.0f, 1.0f }; //default white
    float _x = 0.0f;
    float _y = 0.0f;
    float _depth = 0.0f; //we'll use _depth for z-axis
    float _scale = 1f;
    float _rotation = 0f;

    public GLEntity(){}

    public void update(final double dt) {}

    public void render(final float[] viewportMatrix) {
        final int OFFSET = 0;
        //reset the model matrix and then translate (move) it into world space
        Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
        Matrix.translateM(modelMatrix, OFFSET, _x, _y, _depth);
        //viewportMatrix * modelMatrix combines into the viewportModelMatrix
        //NOTE: projection matrix on the left side and the model matrix on the right side.
        Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
        //apply a rotation around the Z-axis to our modelMatrix. Rotation is in degrees.
        Matrix.setRotateM(modelMatrix, OFFSET, _rotation, 0, 0, 1.0f);
        //apply scaling to our modelMatrix, on the x and y axis only.
        Matrix.scaleM(modelMatrix, OFFSET, _scale, _scale, 1f);
        //finally, multiply the rotated & scaled model matrix into the model-viewport matrix
        //creating the final rotationViewportModelMatrix that we pass on to OpenGL
        Matrix.multiplyMM(rotationViewportModelMatrix, OFFSET, viewportModelMatrix, OFFSET, modelMatrix, OFFSET);

        GLManager.draw(_mesh, rotationViewportModelMatrix, _color);
    }

    public void onCollision(final GLEntity that) {}

    public void setColors(final float[] colors){
        Objects.requireNonNull(colors);
        assert(colors.length >= 4);
        setColors(colors[0], colors[1], colors[2], colors[3]);
    }
    public void setColors(final float r, final float g, final float b, final float a){
        _color[0] = r; //red
        _color[1] = g; //green
        _color[2] = b; //blue
        _color[3] = a; //alpha (transparency)
    }
}
