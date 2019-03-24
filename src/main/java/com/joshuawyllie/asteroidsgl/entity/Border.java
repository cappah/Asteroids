package com.joshuawyllie.asteroidsgl.entity;

import android.opengl.GLES20;

import com.joshuawyllie.asteroidsgl.util.Utils;

public class Border extends GLEntity {
    public static float BORDER_MARGIN = 1f;

    public Border(final float x, final float y, final float worldWidth, final float worldHeight) {
        super(x, y);
        _width = worldWidth - BORDER_MARGIN; //-1 so the border isn't obstructed by the screen edge
        _height = worldHeight - BORDER_MARGIN;
        setColors(1f, 0f, 0f, 1f); //RED for visibility
        mesh = new Mesh(Mesh.generateLinePolygon(4, 10.0), GLES20.GL_LINES);
        mesh.rotateZ(45 * Utils.TO_RAD);
        mesh.setWidthHeight(_width, _height); //will automatically normalize the mesh!
    }

    public void updateDimensions(final float x, final float y, final float worldWidth, final float worldHeight) {
        _x = x;
        _y = y;
        _width = worldWidth - BORDER_MARGIN; //-1 so the border isn't obstructed by the screen edge
        _height = worldHeight - BORDER_MARGIN;
        setColors(1f, 0f, 0f, 1f); //RED for visibility
        mesh = new Mesh(Mesh.generateLinePolygon(4, 10.0), GLES20.GL_LINES);
        mesh.rotateZ(45 * Utils.TO_RAD);
        mesh.setWidthHeight(_width, _height); //will automatically normalize the mesh!
    }
}
