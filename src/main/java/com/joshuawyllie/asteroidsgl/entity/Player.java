package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.joshuawyllie.asteroidsgl.graphic.GLManager;
import com.joshuawyllie.asteroidsgl.util.Utils;

public class Player extends GLEntity {
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds. TODO: game play setting!
    private static final float INIT_WIDTH = 8f;
    private static final float INIT_HEIGHT = 12f;
    private float _bulletCooldown = 0;
    private static final String TAG = "Player";
    static final float ROTATION_VELOCITY = 360f; //TODO: game play values!
    static final float THRUST = 8f;
    static final float DRAG = 0.99f;
    private boolean isBoosting = false;
    private Flame flame = null;
    private float[] flameColor = {1f, 0, 1f, 0};

    public Player(final float x, final float y) {
        super();
        _x = x;
        _y = y;
        _width = INIT_WIDTH; //TODO: gameplay values!
        _height = INIT_HEIGHT;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
        flame = new Flame(_x, _y);
    }

    @Override
    public void update(double dt) {
        _rotation += (dt * ROTATION_VELOCITY) * game.inputManager._horizontalFactor;
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
}
