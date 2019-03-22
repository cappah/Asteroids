package com.joshuawyllie.asteroidsgl.event;

public class Event {
    private EventType type = null;

    public Event(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }
}
