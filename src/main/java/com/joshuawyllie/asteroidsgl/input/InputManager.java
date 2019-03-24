package com.joshuawyllie.asteroidsgl.input;

import android.view.MotionEvent;
import android.view.View;

public class InputManager implements View.OnTouchListener {
    public float _verticalFactor = 0.0f;
    public float _horizontalFactor = 0.0f;
    public boolean _pressingA = false;
    public boolean _pressingB = false;
    private boolean pressing = false;

    public void onStart() {
    }

    public void onStop() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            pressing = true;
        } else if (action == MotionEvent.ACTION_UP) {
            pressing = false;
        }
        return false;
    }

    public boolean isPressing() {
        return pressing;
    }
}
