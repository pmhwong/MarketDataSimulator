package org.philip;

public interface IEventHandler<T> {
    void onEvent(T event);
}
