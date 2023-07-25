package ru.practicum.shareit.util;

public class Counter {
    long id = 1;

    public long createId() {
        return id++;
    }
}
