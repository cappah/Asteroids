package com.joshuawyllie.asteroidsgl.entity;

import com.joshuawyllie.asteroidsgl.GLManager;
import com.joshuawyllie.asteroidsgl.Game;

import java.util.Objects;

public class GLEntity {
    public static Game _game = null; //shared ref, managed by the Game-class!
    Mesh _mesh = null;
    float _color[] = { 1.0f, 1.0f, 1.0f, 1.0f }; //default white
    float _x = 0.0f;
    float _y = 0.0f;
    float _depth = 0.0f; //we'll use _depth for z-axis
    float _scale = 1f;
    float _rotation = 0f;

    public GLEntity(){}

    public void update(final double dt) {}

    public void render(){
        GLManager.draw(_mesh, _color);
    }

    public void onCollision(final GLEntity that) {}

    public void setColors(final float[] colors){
        Objects.requireNonNull(colors);
        assert(colors.length >= 4);
        setColors(colors[0], colors[1], colors[2], colors[3]);
    }
    public void setColors(final float r, final float g, final float b, final float a){
        _color[0] = r; //red
        _color[1] = g; //green
        _color[2] = b; //blue
        _color[3] = a; //alpha (transparency)
    }
}
