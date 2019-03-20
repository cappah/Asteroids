package com.joshuawyllie.asteroidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.joshuawyllie.asteroidsgl.entity.Asteroid;
import com.joshuawyllie.asteroidsgl.entity.Border;
import com.joshuawyllie.asteroidsgl.entity.Bullet;
import com.joshuawyllie.asteroidsgl.entity.GLEntity;
import com.joshuawyllie.asteroidsgl.entity.Player;
import com.joshuawyllie.asteroidsgl.entity.Star;
import com.joshuawyllie.asteroidsgl.graphic.GLManager;
import com.joshuawyllie.asteroidsgl.graphic.Hud;
import com.joshuawyllie.asteroidsgl.display.ViewPort;
import com.joshuawyllie.asteroidsgl.input.InputManager;
import com.joshuawyllie.asteroidsgl.util.Utils;

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
    private static final int STAR_COUNT = 100;
    private static final int ASTEROID_COUNT = 10;
    private static final int BULLET_COUNT = (int) (Bullet.TIME_TO_LIVE / Player.TIME_BETWEEN_SHOTS) + 1;


    public InputManager inputManager = new InputManager(); //empty but valid default    //todo: make private
    private Hud hud = new Hud();
    private ViewPort viewPort = null;

    // entities
    private Border border;
    private GLEntity player;
    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    Bullet[] _bullets = new Bullet[BULLET_COUNT];


    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private float[] viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera
    //storage
    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/
    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;



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
        GLEntity.setGame(this);
        viewPort = new ViewPort(720, 1080, METERS_TO_SHOW_X, METERS_TO_SHOW_Y, getHolder());
        border = new Border(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, WORLD_WIDTH, WORLD_HEIGHT);
        player = new Player(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        Random r = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star(r.nextInt((int) WORLD_WIDTH), r.nextInt((int) WORLD_HEIGHT)));
        }
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            asteroids.add(new Asteroid(r.nextInt((int) WORLD_WIDTH), r.nextInt((int) WORLD_HEIGHT), i + 3));
        }
        for (int i = 0; i < BULLET_COUNT; i++) {
            _bullets[i] = new Bullet();
        }
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
        final double newTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        accumulator += frameTime;
        while (accumulator >= dt) {
            for (final Asteroid a : asteroids) {
                a.update(dt);
            }
            player.update(dt);
            hud.update(dt);
            for (final Bullet b : _bullets) {
                if (!b.isAlive()) {
                    continue;
                } //skip
                b.update(dt);
            }
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
        hud.render(viewportMatrix);
        for (final Bullet b : _bullets) {
            if (!b.isAlive()) {
                continue;
            } //skip
            b.render(viewportMatrix);
        }
        player.render(viewportMatrix);
    }


    public boolean maybeFireBullet(final GLEntity source) {
        for (final Bullet b : _bullets) {
            if (!b.isAlive()) {
                b.fireFrom(source);
                return true;
            }
        }
        return false;
    }

    public void setInputManager(final InputManager input) {
        inputManager = input;
    }

    public InputManager getInputManager() {
        return this.inputManager;
    }
}
