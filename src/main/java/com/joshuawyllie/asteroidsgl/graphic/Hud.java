package com.joshuawyllie.asteroidsgl.graphic;

import com.joshuawyllie.asteroidsgl.display.ViewPort;
import com.joshuawyllie.asteroidsgl.entity.Text;
import com.joshuawyllie.asteroidsgl.util.Utils;

import java.util.HashMap;

enum TextKey {
    FPS,
    SCORE
}

public class Hud {
    private static final String FPS_TEXT = "FPS: %s";
    private static final float MARGIN = 10;
    private double fpsTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;   //todo: put in utils
    private int fpsCounter = 0;
    private String fps = "0";
    private HashMap<TextKey, Text> texts = new HashMap<>();

    public Hud() {
        texts.put(TextKey.FPS, new Text(String.format(FPS_TEXT, fps), MARGIN,ViewPort.WORLD_HEIGHT - Text.GLYPH_HEIGHT));
    }

    public void render(final float[] viewportMatrix) {
        updateFPS();
        for (final Text text : texts.values()) {
            text.render(viewportMatrix);
        }
    }

    public void update(double dt) {
    }

    private void updateFPS() {
        final double timeNow = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;
        if (timeNow - fpsTime > 1f) {
            fpsTime = timeNow;
            texts.get(TextKey.FPS).setString(String.format(FPS_TEXT, Integer.toString(fpsCounter)));
            fpsCounter = 0;
        }
        fpsCounter++;
    }
}
