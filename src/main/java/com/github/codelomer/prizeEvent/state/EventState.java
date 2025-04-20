package com.github.codelomer.prizeEvent.state;

import com.github.codelomer.prizeEvent.manager.EventManager;

public interface EventState {
    void enterState(EventManager eventManager);
    void leaveState(EventManager eventManager);
    EventStatus getStatus();
    long timeLeft();

}
