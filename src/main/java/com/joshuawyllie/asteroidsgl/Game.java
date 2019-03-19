package com.joshuawyllie.asteroidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.joshuawyllie.asteroidsgl.entity.Asteroid;
import com.joshuawyllie.asteroidsgl.entity.Border;
import com.joshuawyllie.asteroidsgl.entity.GLEntity;
import com.joshuawyllie.asteroidsgl.entity.Player;
import com.joshuawyllie.asteroidsgl.entity.Star;
import com.joshuawyllie.asteroidsgl.entity.Text;
import com.joshuawyllie.asteroidsgl.graphic.GLManager;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final float WORLD_WIDTH = 160; //all dimensions are in meters
    public static final float WORLD_HEIGHT = 90f;
    public static final float METERS_TO_SHOW_X = 160; //160m x 90m in view
    public static final float METERS_TO_SHOW_Y = 90f; //TODO: calculate to match screen aspect ratio
    private static final int BG_COLOUR = Color.rgb(135, 206, 235);
    public static final long SECOND_IN_NANOSECONDS = 1000000000;
    public static final long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static final float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    private static final int STAR_COUNT = 100;
    private static final int ASTEROID_COUNT = 10;

    private Border border;
    private GLEntity player;
    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    private ArrayList<Text> _texts = new ArrayList<>();

    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private float[] viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera
    //storage
    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/
    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;

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
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.
        border = new Border(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, WORLD_WIDTH, WORLD_HEIGHT);
        player = new Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        Random r = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star(r.nextInt((int) WORLD_WIDTH), r.nextInt((int) WORLD_HEIGHT)));
        }
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            asteroids.add(new Asteroid(r.nextInt((int) WORLD_WIDTH), r.nextInt((int) WORLD_HEIGHT), i + 3));
        }

        final String s1 = "HELLO WORLD";
        final String s2 = "0123456789";
        final String s3 = ", - . : = ? [ ~";
        final String s4 = "ABCDEFGHIJKLMNOPQRSTUVXYZ";
        _texts.add(new Text(s1, 8, 8));
        _texts.add(new Text(s2, 8, 16));
        _texts.add(new Text(s3, 8, 24));
        _texts.add(new Text(s4, 8, 32));

        setRenderer(this);
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
        update(); //TODO: move updates away from the render thread...
        render();
    }

    private void update() {
        final double newTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        accumulator += frameTime;
        while (accumulator >= dt) {
            for (final Asteroid a : asteroids) {
                a.update(dt);
            }
            player.update(dt);
            accumulator -= dt;
        }
    }

    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.
        //TODO: encapsulate this in a Camera-class, let it "position" itself relative to an entity
        final int offset = 0;
        final float left = 0;
        final float right = METERS_TO_SHOW_X;
        final float bottom = METERS_TO_SHOW_Y;
        final float top = 0;
        final float near = 0f;
        final float far = 1f;
        Matrix.orthoM(viewportMatrix, offset, left, right, bottom, top, near, far);

        border.render(viewportMatrix);
        for (final Asteroid a : asteroids) {
            a.render(viewportMatrix);
        }
        for (final Star s : _stars) {
            s.render(viewportMatrix);
        }
        for (final Text t : _texts) {
            t.render(viewportMatrix);
        }
        player.render(viewportMatrix);
    }
}
