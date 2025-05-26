package io.github.zapolyarnydev.news;

public enum NewsFrequency {

    VERY_OFTEN(5),
    OFTEN(30),
    HOURLY(60),
    SOMETIMES(180),
    RARELY(360);

    private final int minutes;

    NewsFrequency(int minutes) {
        this.minutes = minutes;
    }
}
