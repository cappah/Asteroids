package com.joshuawyllie.asteroidsgl.event;

import com.joshuawyllie.asteroidsgl.entity.GLEntity;

import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    private EventType type = null;
    private ArrayList<GLEntity> entities = new ArrayList<>();

    public Event(EventType type, GLEntity... entities) {
        this.type = type;
        if (entities != null) {
            this.entities.addAll(Arrays.asList(entities));
        }
    }

    public EventType getType() {
        return type;
    }

    public ArrayList<GLEntity> getEntitiesInvolved() {
        return entities;
    }
}
