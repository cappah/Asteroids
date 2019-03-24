package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.event.Event;
import com.joshuawyllie.asteroidsgl.event.EventType;
import com.joshuawyllie.asteroidsgl.util.Utils;

public class Bullet extends GLEntity {
    private static Mesh BULLET_MESH = new Mesh(Mesh.POINT, GLES20.GL_POINTS); //Q&D pool, Mesh.POINT is just [0,0,0] float array
    private static final float SPEED = 120f;
    public static final float TIME_TO_LIVE = 1.7f; //seconds

    public float _ttl = TIME_TO_LIVE;

    public Bullet() {
        setColors(1, 0, 1, 1);
        mesh = BULLET_MESH; //all bullets use the exact same mesh
    }

    public void fireFrom(GLEntity source) {
        final float theta = source._rotation * (float) Utils.TO_RAD;
        _x = source._x + (float) Math.sin(theta) * (source._height * 0.5f);     // changing this to height corrected the bullet vector
        _y = source._y - (float) Math.cos(theta) * (source._height * 0.5f);
        _velX = source._velX;
        _velY = source._velY;
        _velX += (float) Math.sin(theta) * SPEED;
        _velY -= (float) Math.cos(theta) * SPEED;
        _ttl = TIME_TO_LIVE;
        game.broadcastEvent(new Event(EventType.SHOOT));
    }

    public boolean isAlive() {
        return _ttl > 0;
    }

    @Override
    public boolean isDead() {
        return _ttl < 0;
    }

    @Override
    public void update(double dt) {
        if (_ttl > 0) {
            _ttl -= dt;
            super.update(dt);
        }
    }

    @Override
    public void render(final float[] viewportMatrix) {
        if (_ttl > 0) {
            super.render(viewportMatrix);
        }
    }
}