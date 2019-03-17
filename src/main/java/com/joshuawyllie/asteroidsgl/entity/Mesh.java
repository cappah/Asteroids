package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh {
    private static final String TAG = "Mesh";
    // find the size of the float type, in bytes
    public static final int SIZE_OF_FLOAT = Float.SIZE / Byte.SIZE; //32bit/8bit = 4 bytes
    // number of coordinates per vertex in our meshes
    public static final int COORDS_PER_VERTEX = 3; //X, Y, Z
    // number of bytes per vertex
    public static final int VERTEX_STRIDE = COORDS_PER_VERTEX * SIZE_OF_FLOAT;

    public FloatBuffer _vertexBuffer = null;
    public int _vertexCount = 0;
    public int _drawMode = GLES20.GL_TRIANGLES;

    public Mesh(final float[] geometry) {
        init(geometry, GLES20.GL_TRIANGLES);
    }

    public Mesh(final float[] geometry, final int drawMode) {
        init(geometry, drawMode);
    }

    private void init(final float[] geometry, final int drawMode) {
        setVertices(geometry);
        setDrawmode(drawMode);
    }

    public void setDrawmode(int drawMode) {
        assert (drawMode == GLES20.GL_TRIANGLES
                || drawMode == GLES20.GL_LINES
                || drawMode == GLES20.GL_POINTS);
        _drawMode = drawMode;
    }

    public void setVertices(final float[] geometry) {
        // create a floating point buffer from a ByteBuffer
        _vertexBuffer = ByteBuffer.allocateDirect(geometry.length * SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder()) // use the device hardware's native byte order
                .asFloatBuffer();
        _vertexBuffer.put(geometry); //add the coordinates to the FloatBuffer
        _vertexBuffer.position(0); // set the buffer to read the first coordinate
        _vertexCount = geometry.length / COORDS_PER_VERTEX;
    }
}
