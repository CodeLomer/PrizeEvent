package com.github.codelomer.prizeEvent.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.UUID;
@RequiredArgsConstructor
@Getter
public class EventTicket {
    private final @NonNull UUID owner;
    private final @NonNull UUID id;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventTicket that = (EventTicket) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
