package com.joshuawyllie.asteroidsgl.entity;

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
}
