package com.joshuawyllie.asteroidsgl.graphic;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.joshuawyllie.asteroidsgl.entity.Mesh;
import com.joshuawyllie.asteroidsgl.graphic.GLManager;

public class HealthBar {
    private static final float SCALE = 1f;
    private static final double WIDTH = 3F;
    private static final double HEIGHT = 6F;
    private static final float SPACING = 1f;
    public static final float[] modelMatrix = new float[4 * 4];
    public static final float[] viewportModelMatrix = new float[4 * 4];
    private int health = 0;
    private Mesh healthIcon = null;
    private float x = 0;
    private float y = 0;
    private float[] colour = {1f, 1f, 1f, 1f};

    public HealthBar(final float x, final float y, final int initHealth) {
        this.x = x;
        this.y = y;
        this.health = initHealth;
        float vertices[] = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        healthIcon = new Mesh(vertices, GLES20.GL_TRIANGLES);
        healthIcon.setWidthHeight(WIDTH, HEIGHT);
        healthIcon.flipY();
    }

    public void setPos(final float x, final float y) {
        this.x = x;
        this.y = y;
    }

    public void update(double dt, int health) {
        this.health = health;
    }

    public void render(float[] viewportMatrix) {
       for (int i = 0; i < health; i++) {
            final int OFFSET = 0;
            Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
            Matrix.translateM(modelMatrix, OFFSET, x + (i * (healthIcon._width + SPACING)) , y, 0f);
            Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
            GLManager.draw(healthIcon, viewportModelMatrix, colour);
        }
    }
}
