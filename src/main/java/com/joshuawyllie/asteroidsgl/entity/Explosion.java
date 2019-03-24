package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.joshuawyllie.asteroidsgl.graphic.GLManager;
import com.joshuawyllie.asteroidsgl.util.Random;

import java.util.ArrayList;

public class Explosion {
    private static final int NUM_PARTICLES = 15;
    private static final float MAX_RADIUS = 1.2f;
    private static final float PARTICLE_VEL_SCALAR = 12f;
    private static final double TIME_TO_LIVE = 0.8F; //seconds
    private static Mesh pointStatic = new Mesh(Mesh.POINT, GLES20.GL_POINTS);
    private ArrayList<Particle> particles = new ArrayList<>();
    private float posX = 0;
    private float posY = 0;
    private float scale = 1f;
    private double ttl = TIME_TO_LIVE;
    private static final float[] modelMatrix = new float[4 * 4];
    private static final float[] viewportModelMatrix = new float[4 * 4];
    private float[] colour = {1f, 1f, 1f, 1f};

    public Explosion(final float posX, final float posY) {
        this.posX = posX;
        this.posY = posY;
        for (int i = 0; i < NUM_PARTICLES; i++) {
            final float particleX = posX + Random.between(-MAX_RADIUS, MAX_RADIUS);
            final float particleY = posY + Random.between(-MAX_RADIUS, MAX_RADIUS);
            final float particleVelX = (particleX - posX) * PARTICLE_VEL_SCALAR;
            final float particleVelY = (particleY - posY) * PARTICLE_VEL_SCALAR;
            particles.add(new Particle(particleX, particleY, particleVelX, particleVelY));
        }
    }

    public void update(final double dt) {
        for (Particle particle : particles) {
            particle.update(dt);
        }
        ttl -= dt;
    }

    public void render(final float[] viewportMatrix) {
        if (isAlive()) {
            for (Particle particle : particles) {
                final int OFFSET = 0;
                //reset the model matrix and then translate (move) it into world space
                Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
                Matrix.translateM(modelMatrix, OFFSET, particle.x, particle.y, 0f);
                //viewportMatrix * modelMatrix combines into the viewportModelMatrix
                //NOTE: projection matrix on the left side and the model matrix on the right side.
                Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
                //apply scaling to our modelMatrix, on the x and y axis only.
                Matrix.scaleM(modelMatrix, OFFSET, scale, scale, 1f);
                //finally, multiply the rotated & scaled model matrix into the model-viewport matrix
                // Matrix.multiplyMM(scaleViewportModelMatrix, OFFSET, viewportModelMatrix, OFFSET, modelMatrix, OFFSET);
                GLManager.draw(pointStatic, viewportModelMatrix, colour);
            }
        }
    }

    public boolean isAlive() {
        return ttl > 0;
    }

    public boolean isDead() {
        return ttl < 0;
    }

    private class Particle {
        private float x = 0;
        private float y = 0;
        private float velX = 1f;
        private float velY = 1f;

        Particle(final float x, final float y, final float velX, final float velY) {
            this.x = x;
            this.y = y;
            this.velX = velX;
            this.velY = velY;
        }

        void update(final double dt) {
            x += velX * dt;
            y += velY * dt;
        }
    }
}
