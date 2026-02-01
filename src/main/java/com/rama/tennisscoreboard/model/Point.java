package com.rama.tennisscoreboard.model;

import lombok.Getter;

@Getter
public enum Point {
    LOVE("0"),
    FIFTEEN("15"),
    THIRTY("30"),
    FORTY("40"),
    ADVANTAGE("AD");

    private final String displayed;

    Point(String displayed) {
        this.displayed = displayed;
    }

    public boolean isStandardPoint() {
        return this == LOVE || this == FIFTEEN || this == THIRTY;
    }

    public Point next() {
        return switch (this) {
            case LOVE -> FIFTEEN;
            case FIFTEEN -> THIRTY;
            case THIRTY -> FORTY;
            default -> throw new IllegalStateException("No standart next point after " + this);
        };
    }
}
