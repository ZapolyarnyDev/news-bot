package io.github.zapolyarnydev.model;

import lombok.Getter;

@Getter
public enum NewsFrequency {

    VERY_OFTEN(1),
    OFTEN(30),
    HOURLY(60),
    SOMETIMES(180),
    RARELY(360);

    private final int minutes;

    NewsFrequency(int minutes) {
        this.minutes = minutes;
    }
}
