package com.joshuawyllie.asteroidsgl.graphic;

import com.joshuawyllie.asteroidsgl.display.ViewPort;
import com.joshuawyllie.asteroidsgl.entity.Border;
import com.joshuawyllie.asteroidsgl.entity.Player;
import com.joshuawyllie.asteroidsgl.entity.Text;
import com.joshuawyllie.asteroidsgl.util.Utils;

import java.util.HashMap;

enum TextKey {
    FPS,
    SCORE,
    LEVEL
}

public class Hud {
    private static final String FPS_TEXT = "FPS: %s";
    private static final String SCORE_TEXT = "Score: %s";
    private static final float MARGIN = 2f + Border.BORDER_MARGIN;
    private double fpsTime = System.nanoTime() * Utils.NANOSECONDS_TO_SECONDS;
    private int fpsCounter = 0;
    private String fps = "0";
    private HashMap<TextKey, Text> texts = new HashMap<>();
    private HealthBar healthBar;

    public Hud(int initHealth) {
        healthBar = new HealthBar(MARGIN * 2, MARGIN + Text.GLYPH_HEIGHT, initHealth);
        reset();
    }

    private void reset() {
        texts.clear();
        texts.put(TextKey.FPS, new Text(String.format(FPS_TEXT, fps), MARGIN,ViewPort.WORLD_HEIGHT - Text.GLYPH_HEIGHT));
        texts.put(TextKey.SCORE, new Text(String.format(SCORE_TEXT, Player.INIT_HEALTH), MARGIN, MARGIN));
        texts.put(TextKey.LEVEL, new Text("Level: ", MARGIN + FPS_TEXT.length() * Text.GLYPH_WIDTH, MARGIN));
    }

    public void update(double dt, int score, int health) {
        texts.get(TextKey.SCORE).setString(String.format(SCORE_TEXT, score));
        healthBar.update(dt, health);
    }

    public void render(final float[] viewportMatrix) {
        updateFPS();
        for (final Text text : texts.values()) {
            text.render(viewportMatrix);
        }
        healthBar.render(viewportMatrix);

    }

    public void updateDimensions() {
        reset();
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

