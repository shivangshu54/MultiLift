package com.shivangshu.multilift.commons;

public enum RequestedDirection {

    UP(1),
    DOWN(2),
    NONE_ERROR(0);

    private int value;

    RequestedDirection(int value) {
        this.value = value;
    }
}
