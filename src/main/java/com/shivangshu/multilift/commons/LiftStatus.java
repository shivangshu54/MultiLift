package com.shivangshu.multilift.commons;

public enum LiftStatus {

    MOVING_UP(1),
    MOVING_DOWN(2),
    IDLE(3),
    ERROR_NONE(0);

    private int value;

    LiftStatus(int value) {
        this.value = value;
    }
}
