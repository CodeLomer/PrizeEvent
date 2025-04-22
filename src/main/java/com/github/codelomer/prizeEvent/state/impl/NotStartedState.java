package com.github.codelomer.prizeEvent.state.impl;

import com.github.codelomer.prizeEvent.manager.EventManager;
import com.github.codelomer.prizeEvent.state.EventState;
import com.github.codelomer.prizeEvent.state.EventStatus;

public class NotStartedState implements EventState {
    @Override
    public void enterState(EventManager eventManager) {

    }

    @Override
    public void leaveState(EventManager eventManager) {

    }

    @Override
    public EventStatus getStatus() {
        return EventStatus.NOT_STARTED;
    }

    @Override
    public long timeLeft() {
        return 0;
    }
}
