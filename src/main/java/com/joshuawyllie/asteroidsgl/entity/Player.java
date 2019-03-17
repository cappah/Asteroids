package com.joshuawyllie.asteroidsgl.entity;

import android.os.SystemClock;

import com.joshuawyllie.asteroidsgl.Game;

public class Player extends GLEntity {
    private static final String TAG = "Player";

    public Player(float x, float y){
        super();
        _x = x;
        _y = y;
        _mesh = new Triangle();
    }

    @Override
    public void update(double dt) {}

    @Override
    public void render(float[] viewportMatrix) {
        final float TO_RADIANS = (float)Math.PI/180.0f;
        final float startPositionX = 0f;
        final float range = Game.METERS_TO_SHOW_X/2; //amplitude of our sine wave (how far to travel)
        final float speed = 360f/2000f; //I want to cover a full revolution (360 degrees) in 2 seconds.
        float angle = (SystemClock.uptimeMillis() * speed) % 360f; //turn linear, ever growing, timestamp into 0-359 range
        angle *= TO_RADIANS; //convert degrees to radians, that's what sin wants.

        //sin() returns a numeric value between [-1.0, 1.0], the sine of the angle given in radians.
        //perfect for moving smoothly up-and-down some range
        _x = startPositionX + (float)Math.sin(angle) * range;

        long time = SystemClock.uptimeMillis() % 5000; //turn a timestamp into 0-4999 ms
        _rotation = (360.0f / 5000.0f) * time; // Do a complete rotation every 5 seconds.

        _scale = 20f; //render at 20x the size

        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix);
    }
}
