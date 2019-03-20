package com.joshuawyllie.asteroidsgl.graphic;

import com.joshuawyllie.asteroidsgl.entity.Text;
import com.joshuawyllie.asteroidsgl.util.Utils;

import java.util.HashMap;

enum TextKey {
    FPS,
    SCORE
}

public class Hud {
    private double fpsTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;   //todo: put in utils
    private int fpsCounter = 0;
    private String fps = "10";
    private HashMap<TextKey, Text> texts = new HashMap<>();

    public Hud() {
        texts.put(TextKey.FPS, new Text(fps, 2, 2));
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
            texts.get(TextKey.FPS).setString(Integer.toString(fpsCounter));
            fpsCounter = 0;
        }
        fpsCounter++;
    }
}
