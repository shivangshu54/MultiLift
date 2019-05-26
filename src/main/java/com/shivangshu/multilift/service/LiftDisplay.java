package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStore;

import java.util.List;

public class LiftDisplay implements ILiftObserver {

    private int id;

    public LiftDisplay(int id) {
        this.id = id;
    }

    @Override
    public void displayInfo() {

    }

    @Override
    public void getLifts() {

    }
}
