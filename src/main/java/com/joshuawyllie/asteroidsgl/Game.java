package com.joshuawyllie.asteroidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.joshuawyllie.asteroidsgl.display.ViewPort;
import com.joshuawyllie.asteroidsgl.entity.Asteroid;
import com.joshuawyllie.asteroidsgl.entity.Border;
import com.joshuawyllie.asteroidsgl.entity.Bullet;
import com.joshuawyllie.asteroidsgl.entity.GLEntity;
import com.joshuawyllie.asteroidsgl.entity.Player;
import com.joshuawyllie.asteroidsgl.entity.Star;
import com.joshuawyllie.asteroidsgl.event.Event;
import com.joshuawyllie.asteroidsgl.event.EventReceiver;
import com.joshuawyllie.asteroidsgl.graphic.GLManager;
import com.joshuawyllie.asteroidsgl.graphic.Hud;
import com.joshuawyllie.asteroidsgl.input.InputManager;
import com.joshuawyllie.asteroidsgl.util.Utils;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    private static final int BG_COLOUR = Color.rgb(135, 206, 235);
    private static final int STAR_COUNT = 100;
    private static final int ASTEROID_COUNT = 10;
    private static final int BULLET_COUNT = (int) (Bullet.TIME_TO_LIVE / Player.TIME_BETWEEN_SHOTS) + 1;

    private Context context = null;
    private ArrayList<EventReceiver> eventReceivers = new ArrayList<>();
    public InputManager inputManager = new InputManager(); //empty but valid default    //todo: make private
    private Hud hud = new Hud();
    private ViewPort viewPort = null;
    private Jukebox jukebox = null;

    // entities
    private Border border;
    private GLEntity player;
    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    Bullet[] _bullets = new Bullet[BULLET_COUNT];

    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/
    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;


    public Game(Context context) {
        super(context);
        init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.
        GLEntity.setGame(this);
        viewPort = new ViewPort(context);
        jukebox = new Jukebox(context);
        // todo: uncomment this line again jukebox.resumeBgMusic();
        eventReceivers.add(jukebox);
        border = new Border(ViewPort.WORLD_WIDTH / 2, ViewPort.WORLD_HEIGHT / 2, ViewPort.WORLD_WIDTH, ViewPort.WORLD_HEIGHT);
        player = new Player(ViewPort.WORLD_WIDTH / 2, ViewPort.WORLD_HEIGHT / 2);
        Random r = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star(r.nextInt((int) ViewPort.WORLD_WIDTH), r.nextInt((int) ViewPort.WORLD_HEIGHT)));
        }
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            asteroids.add(new Asteroid(r.nextInt((int) ViewPort.WORLD_WIDTH), r.nextInt((int) ViewPort.WORLD_HEIGHT), i + 3));
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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        viewPort.onSurfaceCreated(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        viewPort.onSurfaceChanged(width, height);
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
            collisionDetection();
            removeDeadEntities();
            accumulator -= dt;
        }
    }

    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.
        //TODO: encapsulate this in a Camera-class, let it "position" itself relative to an entity


        border.render(viewPort.getViewportMatrix());
        for (final Asteroid a : asteroids) {
            a.render(viewPort.getViewportMatrix());
        }
        for (final Star s : _stars) {
            s.render(viewPort.getViewportMatrix());
        }
        hud.render(viewPort.getViewportMatrix());
        for (final Bullet b : _bullets) {
            if (!b.isAlive()) {
                continue;
            } //skip
            b.render(viewPort.getViewportMatrix());
        }
        player.render(viewPort.getViewportMatrix());
    }

    private void collisionDetection() {
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip dead bullets
            for (final Asteroid a : asteroids) {
                if (b.isColliding(a)) {
                    if (a.isDead()) {
                        continue;
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                }
            }
        }
        for (final Asteroid a : asteroids) {
            if (a.isDead()) {
                continue;
            }
            if (player.isColliding(a)) {
                player.onCollision(a);
                a.onCollision(player);
            }
        }
    }

    public void onEventReceived(Event event) {
        for (EventReceiver eventReceiver : eventReceivers) {
            eventReceiver.onEvent(event);
        }
    }

    private void removeDeadEntities() {
        Asteroid temp;
        final int count = asteroids.size();
        for (int i = count - 1; i >= 0; i--) {
            temp = asteroids.get(i);
            if (temp.isDead()) {
                asteroids.remove(i);
            }
        }
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
