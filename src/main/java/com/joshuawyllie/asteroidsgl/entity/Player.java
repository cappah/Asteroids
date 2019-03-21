package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Utils;

public class Player extends GLEntity {
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds. TODO: game play setting!
    private float _bulletCooldown = 0;
    private static final String TAG = "Player";
    static final float ROTATION_VELOCITY = 360f; //TODO: game play values!
    static final float THRUST = 8f;
    static final float DRAG = 0.99f;

    public Player(final float x, final float y) {
        super();
        _x = x;
        _y = y;
        _width = 8f; //TODO: gameplay values!
        _height = 12f;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
    }

    @Override
    public void update(double dt) {
        _rotation += (dt * ROTATION_VELOCITY) * game.inputManager._horizontalFactor;
        if (game.inputManager._pressingB) {
            final float theta = _rotation * (float) Utils.TO_RAD;
            _velX += (float) Math.sin(theta) * THRUST;
            _velY -= (float) Math.cos(theta) * THRUST;
        }
        _velX *= DRAG;
        _velY *= DRAG;
        _bulletCooldown -= dt;
        if (game.getInputManager()._pressingA && _bulletCooldown <= 0) {
            setColors(1, 0, 1, 1);
            if (game.maybeFireBullet(this)) {
                _bulletCooldown = TIME_BETWEEN_SHOTS;
            }
        } else {
            setColors(1.0f, 1, 1, 1);
        }
        super.update(dt);
    }

    @Override
    public void render(float[] viewportMatrix) {
//        final float TO_RADIANS = (float) Math.PI / 180.0f;
//        final float startPositionX = Game.WORLD_WIDTH / 2;
//        final float range = Game.WORLD_WIDTH / 2; //amplitude of our sine wave (how far to travel)
//        final float speed = 360f / 2000f; //I want to cover a full revolution (360 degrees) in 2 seconds.
//        float angle = (SystemClock.uptimeMillis() * speed) % 360f; //turn linear, ever growing, timestamp into 0-359 range
//        angle *= TO_RADIANS; //convert degrees to radians, that's what sin wants.
//
//        //sin() returns a numeric value between [-1.0, 1.0], the sine of the angle given in radians.
//        //perfect for moving smoothly up-and-down some range
//        _x = startPositionX + (float) Math.sin(angle) * range;
//
//        long time = SystemClock.uptimeMillis() % 5000; //turn a timestamp into 0-4999 ms
//        _rotation = (360.0f / 5000.0f) * time; // Do a complete rotation every 5 seconds.
//
//        _scale = 5f; //render at 20x the size

        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix);
    }
}
