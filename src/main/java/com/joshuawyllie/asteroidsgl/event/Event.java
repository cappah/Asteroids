package com.joshuawyllie.asteroidsgl.event;

import com.joshuawyllie.asteroidsgl.entity.GLEntity;

import org.w3c.dom.Entity;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL;

public class Event {
    private EventType type = null;
    private ArrayList<GLEntity> entities = new ArrayList<>();

    public Event(EventType type, GLEntity... entities) {
        this.type = type;
        if (entities != null) {
            this.entities.addAll(Arrays.asList(entities));
        }
    }

    public Event(EventType type) {
        this(type, null);
    }

    public EventType getType() {
        return type;
    }
}
