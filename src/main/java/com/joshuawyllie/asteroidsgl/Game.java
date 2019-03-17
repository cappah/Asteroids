package com.joshuawyllie.asteroidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.joshuawyllie.asteroidsgl.entity.GLEntity;
import com.joshuawyllie.asteroidsgl.entity.Player;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static float WORLD_WIDTH = 320f; //all dimensions are in meters
    public static float WORLD_HEIGHT = 180f;
    public static float METERS_TO_SHOW_X = 160f; //160m x 90m in view
    public static float METERS_TO_SHOW_Y = 90f; //TODO: calculate to match screen aspect ratio
    private static final int BG_COLOUR = Color.rgb(135, 206, 235);

    private GLEntity player;
    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private float[] viewportMatrix = new float[4*4]; //In essence, it is our our Camera

    public Game(Context context) {
        super(context);
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        player = new Player(0.5f, 0.5f);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        //build program (shaders)
        //tell opengl to use program (our shaders)
        GLManager.buildProgram();
        float red = Color.red(BG_COLOUR) / 255f;
        float green = Color.green(BG_COLOUR) / 255f;
        float blue = Color.blue(BG_COLOUR) / 255f;
        float alpha = 1f;
        GLES20.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(final GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.
        //TODO: encapsulate this in a Camera-class
        final int offset = 0;
        final float near = 0f;
        final float far = 1f;
        final float left = -(METERS_TO_SHOW_X/2f);
        final float right = METERS_TO_SHOW_X/2f;
        final float top = METERS_TO_SHOW_Y/2f; //NOTE: inverted y-axis!
        final float bottom = -(METERS_TO_SHOW_Y/2f); //NOTE: inverted y-axis!
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);
        player.render(viewportMatrix);
    }
}
