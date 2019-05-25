package com.shivangshu.multilift.commons;

public enum LiftPosition {

    UP(1),
    DOWN(2),
    SAME(3),
    NONE_ERROR(0);

    private int value;

    LiftPosition(int value){
        this.value = value;
    }
}
