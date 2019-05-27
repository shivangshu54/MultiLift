package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LiftDisplay implements ILiftObserver {

    Logger log = LoggerFactory.getLogger(LiftDisplay.class);

    private int id;

    public LiftDisplay(int id) {
        this.id = id;
    }

    @Override
    public void displayInfo(Lift lift) {
        log.info("Lift id {} "+ lift.getId() + " is at floor {} "+ lift.getCurrentFloor() + " and is " + lift.getStatus().toString());
    }

}
