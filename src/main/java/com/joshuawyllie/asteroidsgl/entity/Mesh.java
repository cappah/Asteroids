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
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;


    public float _width = 0f;
    public float _height = 0f;
    public float _depth = 0f;
    public float _radius = 0f;
    public Point3D _min = new Point3D(0.0f, 0.0f, 0.0f);
    public Point3D _max = new Point3D(0.0f, 0.0f, 0.0f);

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
        updateBounds();
    }

    public void flipX(){ flip(X); }
    public void flipY(){ flip(Y); }
    public void flipZ(){ flip(Z); }

    public void flip(final int axis){
        assert(axis == X || axis == Y || axis == Z);
        _vertexBuffer.position(0);
        for(int i = 0; i < _vertexCount; i++){
            final int index = i*COORDS_PER_VERTEX + axis;
            final float invertedCoordinate = _vertexBuffer.get(index) * -1;
            _vertexBuffer.put(index, invertedCoordinate);
        }
        updateBounds();
    }

    public void updateBounds(){
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for(int i = 0; i < _vertexCount*COORDS_PER_VERTEX; i+=COORDS_PER_VERTEX) {
            final float x = _vertexBuffer.get(i+X);
            final float y = _vertexBuffer.get(i+Y);
            final float z = _vertexBuffer.get(i+Z);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        _min = new Point3D(minX, minY, minZ);
        _max = new Point3D(maxX, maxY, maxZ);
        _width = maxX - minX;
        _height = maxY - minY;
        _depth = maxZ - minZ;
        _radius = Math.max(Math.max(_width, _height), _depth) * 0.5f;
    }

    public float left() {
        return _min._x;
    }
    public  float right() {
        return _max._x;
    }
    public float top() {
        return _min._y;
    }
    public float bottom() {
        return _max._y;
    }
    public float centerX() {
        return (_width * 0.5f);
    }
    public float centerY() {
        return (_height * 0.5f);
    }
}
