package com.joshuawyllie.asteroidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import com.joshuawyllie.asteroidsgl.display.ViewPort;
import com.joshuawyllie.asteroidsgl.entity.Asteroid;
import com.joshuawyllie.asteroidsgl.entity.Border;
import com.joshuawyllie.asteroidsgl.entity.Bullet;
import com.joshuawyllie.asteroidsgl.entity.Explosion;
import com.joshuawyllie.asteroidsgl.entity.GLEntity;
import com.joshuawyllie.asteroidsgl.entity.Player;
import com.joshuawyllie.asteroidsgl.entity.Star;
import com.joshuawyllie.asteroidsgl.event.Event;
import com.joshuawyllie.asteroidsgl.event.EventReceiver;
import com.joshuawyllie.asteroidsgl.event.EventType;
import com.joshuawyllie.asteroidsgl.graphic.GLManager;
import com.joshuawyllie.asteroidsgl.graphic.Hud;
import com.joshuawyllie.asteroidsgl.input.InputManager;
import com.joshuawyllie.asteroidsgl.util.Random;
import com.joshuawyllie.asteroidsgl.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Game extends GLSurfaceView implements GLSurfaceView.Renderer, EventReceiver {
    private static final String TAG = "Game";
    private static final int BG_COLOUR = Color.rgb(0, 0, 15);
    private static final int STAR_COUNT = 100;
    private static final int INIT_ASTEROID_COUNT = 3;
    private static final int BULLET_COUNT = (int) (Bullet.TIME_TO_LIVE / Player.TIME_BETWEEN_SHOTS) + 1;
    public static final int INIT_LEVEL = 1;
    private static final double GAME_OVER_DELAY = 2f;

    private Context context = null;
    private ArrayList<EventReceiver> eventReceivers = new ArrayList<>();
    private ArrayList<EventReceiver> eventReceiversToRemove = new ArrayList<>();
    private ArrayList<EventReceiver> eventReceiversToAdd = new ArrayList<>();
    private InputManager inputManager = new InputManager(); //empty but valid default
    private Hud hud = null;
    private ViewPort viewPort = null;
    private Jukebox jukebox = null;
    private Queue<Event> eventQueue = new LinkedList<>();

    // entities
    private Border border;
    private Player player;
    private ArrayList<Star> _stars = new ArrayList<>();
    private ArrayList<Asteroid> asteroids = new ArrayList<>();
    Bullet[] _bullets = new Bullet[BULLET_COUNT];
    private ArrayList<Asteroid> asteroidsToAdd = new ArrayList<>();
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private ArrayList<Explosion> explosionsToAdd = new ArrayList<>();

    //trying a fixed time-step with accumulator, courtesy of
    //https://gafferongames.com/post/fix_your_timestep/
    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;
    private int level = INIT_LEVEL;
    private boolean gameIsOver = false;
    private double gameOverDelayCounter = GAME_OVER_DELAY;

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
        viewPort = new ViewPort(context, ViewPort.ViewPortMode.FILL);
        jukebox = new Jukebox(context);
        // todo: uncomment this line again jukebox.resumeBgMusic();
        hud = new Hud(Player.INIT_HEALTH);
        border = new Border(ViewPort.WORLD_WIDTH / 2, ViewPort.WORLD_HEIGHT / 2, ViewPort.WORLD_WIDTH, ViewPort.WORLD_HEIGHT);
        player = new Player(ViewPort.WORLD_WIDTH / 2, ViewPort.WORLD_HEIGHT / 2);
        for (int i = 0; i < STAR_COUNT; i++) {
            _stars.add(new Star(Random.between(0, ViewPort.WORLD_WIDTH), Random.between(0, ViewPort.WORLD_HEIGHT)));
        }
        for (int i = 0; i < INIT_ASTEROID_COUNT + level; i++) {
            asteroids.add(new Asteroid(Random.between(0, ViewPort.WORLD_WIDTH), Random.between(0, ViewPort.WORLD_HEIGHT), Asteroid.INIT_SIZE));
        }
        for (int i = 0; i < BULLET_COUNT; i++) {
            _bullets[i] = new Bullet();
        }
        explosions.add(new Explosion(ViewPort.WORLD_WIDTH / 2, ViewPort.WORLD_HEIGHT / 2));
        registerEventReceiver(this);
        registerEventReceiver(jukebox);
        registerEventReceiver(player);
        registerEventReceiver(hud);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        //build program (shaders)
        //tell opengl to use program (our shaders)
        GLManager.buildProgram(context);
        float red = Color.red(BG_COLOUR) / 255f;
        float green = Color.green(BG_COLOUR) / 255f;
        float blue = Color.blue(BG_COLOUR) / 255f;
        float alpha = 1f;
        GLES20.glClearColor(red, green, blue, alpha);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        viewPort.onSurfaceCreated(displayMetrics.widthPixels, displayMetrics.heightPixels);
        updateDimensions();
    }

    @Override
    public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        viewPort.onSurfaceChanged(width, height);
        updateDimensions();
    }

    private void updateDimensions() {
        border.updateDimensions(ViewPort.WORLD_WIDTH / 2, ViewPort.WORLD_HEIGHT / 2, ViewPort.WORLD_WIDTH, ViewPort.WORLD_HEIGHT);
        hud.updateDimensions();
    }

    @Override
    public void onDrawFrame(final GL10 unused) {
        update();
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
            hud.update(dt, player.getScore(), player.getHealth(), level);
            for (final Bullet b : _bullets) {
                if (!b.isAlive()) {
                    continue;
                } //skip
                b.update(dt);
            }
            for (Explosion explosion : explosions) {
                explosion.update(dt);
            }
            updateEventQueue();
            collisionDetection();
            removeDeadEntities();
            addEntitiesToAdd();
            updateLevel();
            accumulator -= dt;
        }
    }

    private void updateEventQueue() {
        while (!eventQueue.isEmpty()) {
            Event currentEvent = eventQueue.remove();
            eventReceivers.removeAll(eventReceiversToRemove);
            eventReceiversToRemove.clear();
            eventReceivers.addAll(eventReceiversToAdd);
            eventReceiversToAdd.clear();
            for (EventReceiver eventReceiver : eventReceivers) {
                eventReceiver.onEvent(currentEvent);
            }
        }
    }

    private void updateLevel() {
        if (asteroids.isEmpty()) {
            for (int i = 0; i < INIT_ASTEROID_COUNT + level; i++) {
                asteroids.add(new Asteroid(Random.between(0, ViewPort.WORLD_WIDTH), Random.between(0, ViewPort.WORLD_HEIGHT), Asteroid.INIT_SIZE));
            }
            level++;
        }
        if (gameIsOver) {
            gameOverDelayCounter -= dt;
            if (inputManager.isPressing() && gameOverDelayCounter < 0) {
                gameIsOver = false;
                gameOverDelayCounter = GAME_OVER_DELAY;
                broadcastEvent(new Event(EventType.RESTART));
            }
        }
    }

    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color
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
        for (Explosion explosion : explosions) {
            explosion.render(viewPort.getViewportMatrix());
        }
    }

    private void collisionDetection() {
        for (final Bullet bullet : _bullets) {
            if (bullet.isDead()) {
                continue;
            } //skip dead bullets
            for (final Asteroid asteroid : asteroids) {
                if (bullet.isColliding(asteroid)) {
                    if (asteroid.isDead() || asteroid.isRecovering()) {
                        continue;
                    }
                    broadcastEvent(new Event(EventType.ASTEROID_SHOT, asteroid, bullet));
                }
            }
        }
        for (final Asteroid asteroid : asteroids) {
            if (asteroid.isDead()) {
                continue;
            }
            if (player.isColliding(asteroid)) {
                broadcastEvent(new Event(EventType.PLAYER_HIT, asteroid));
            }
        }
    }

    private void onAsteroidShot(Asteroid asteroid, Bullet bullet) {
        bullet.onCollision(asteroid); //notify each entity so they can decide what to do
        asteroid.onCollision(bullet);
        if (asteroid.getSize() > 1) {
            asteroidsToAdd.add(new Asteroid(asteroid.getX(), asteroid.getY(), asteroid.getSize() - 1));
            asteroidsToAdd.add(new Asteroid(asteroid.getX(), asteroid.getY(), asteroid.getSize() - 1));
        }
        explosionsToAdd.add(new Explosion(asteroid.getX(), asteroid.getY()));

    }

    private void onPlayerAsteroidCollision(Asteroid asteroid) {
        player.onCollision(asteroid);
        explosionsToAdd.add(new Explosion(player.getPos().x, player.getPos().y));
        asteroid.onCollision(player);
    }

    private void removeDeadEntities() {
        Asteroid tempA;
        int count = asteroids.size();
        for (int i = count - 1; i >= 0; i--) {
            tempA = asteroids.get(i);
            if (tempA.isDead()) {
                asteroids.remove(i);
            }
        }
        Explosion tempE;
        count = explosions.size();
        for (int i = count - 1; i >= 0; i--) {
            tempE = explosions.get(i);
            if (tempE.isDead()) {
                explosions.remove(i);
            }
        }
    }

    private void addEntitiesToAdd() {
        asteroids.addAll(asteroidsToAdd);
        asteroidsToAdd.clear();
        explosions.addAll(explosionsToAdd);
        explosionsToAdd.clear();
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

    public void broadcastEvent(Event event) {
        eventQueue.add(event);
    }

    public void registerEventReceiver(EventReceiver eventReceiver) {
        eventReceiversToAdd.add(eventReceiver);
    }

    public void unregisterEventReceiver(EventReceiver eventReceiver) {
        eventReceiversToRemove.remove(eventReceiver);
    }

    @Override
    public void onEvent(Event event) {
        try {
            switch (event.getType()) {
                case ASTEROID_SHOT:
                    onAsteroidShot((Asteroid) event.getEntitiesInvolved().get(0), (Bullet) event.getEntitiesInvolved().get(1));
                    break;
                case PLAYER_HIT:
                    onPlayerAsteroidCollision((Asteroid) event.getEntitiesInvolved().get(0));
                    break;
                case DEATH:
                    gameIsOver = true;
                    break;
                case RESTART:
                    gameIsOver = false;
                    level = INIT_LEVEL;
                    break;
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage());
        }
    }

}
