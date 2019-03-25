package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;
import android.util.Log;

import com.joshuawyllie.asteroidsgl.event.Event;
import com.joshuawyllie.asteroidsgl.event.EventType;
import com.joshuawyllie.asteroidsgl.util.Utils;

public class Player extends GLEntity {
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds
    private static final float INIT_WIDTH = 4f;
    private static final float INIT_HEIGHT = 6f;
    public static final int INIT_HEALTH = 3;
    private static final int SCORE_MULTIPLIER = 10;
    private float _bulletCooldown = 0;
    private static final String TAG = "Player";
    static final float ROTATION_VELOCITY = 360f;
    static final float THRUST = 3.5f;
    static final float DRAG = 0.987f;
    private boolean isBoosting = false;
    private Flame flame = null;
    private int score = 0;
    private int health = INIT_HEALTH;

    public Player(final float x, final float y) {
        super(x, y);
        _width = INIT_WIDTH;
        _height = INIT_HEIGHT;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        mesh.setWidthHeight(_width, _height);
        mesh.flipY();
        flame = new Flame(_x, _y);
        flame.setSize(_width * 0.5f, _height * 0.5f);
    }

    @Override
    public void update(double dt) {
        _rotation += (dt * ROTATION_VELOCITY) * game.getInputManager()._horizontalFactor;
        isBoosting = game.getInputManager()._pressingB;
        if (isBoosting) {
            final float theta = _rotation * (float) Utils.TO_RAD;
            _velX += (float) Math.sin(theta) * THRUST;
            _velY -= (float) Math.cos(theta) * THRUST;
        }
        _velX *= DRAG;
        _velY *= DRAG;
        _bulletCooldown -= dt;
        if (game.getInputManager()._pressingA && _bulletCooldown <= 0) {
            if (game.maybeFireBullet(this)) {
                _bulletCooldown = TIME_BETWEEN_SHOTS;
            }
        }
        super.update(dt);
        flame.followEntity(this);
        flame.update(dt);
    }

    @Override
    public void render(float[] viewportMatrix) {
        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix);
        if (isBoosting) {
            flame.render(viewportMatrix);
        }
    }

    @Override
    public boolean isColliding(GLEntity that) {
        if (this == that) {
            throw new AssertionError("isColliding: You shouldn't test Entities against themselves!");
        }
        return GLEntity.isBoundingSpheresOverlapping(this, that);
    }

    @Override
    public void onEvent(Event event) {
        super.onEvent(event);
        try {
            switch (event.getType()) {
                case ASTEROID_SHOT:
                    if (event.getEntitiesInvolved().size() > 1) {
                        final int size = ((Asteroid) event.getEntitiesInvolved().get(0)).getSize();
                        score += size * SCORE_MULTIPLIER;
                    }
                    break;
                case DEATH:
                    break;
                case RESTART:
                    health = INIT_HEALTH;
                    score = 0;
                    _x = INIT_X;
                    _y = INIT_Y;
                    break;
            }
        } catch (Exception exception) {
            Log.e(TAG, exception.getMessage());
        }
    }

    @Override
    public void onCollision(GLEntity that) {
        health--;
        if (health <= 0) {
            _isAlive = false;
            game.broadcastEvent(new Event(EventType.DEATH, this));
        }
    }

    public int getScore() {
        return score;
    }

    public int getHealth() {
        return health;
    }
}
