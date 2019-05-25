package com.shivangshu.multilift.commons;

import com.shivangshu.multilift.service.ILiftObservable;
import com.shivangshu.multilift.service.ILiftObserver;
import lombok.Data;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
public class Lift implements ILiftObservable {

    private int id;
    int currentFloor;
    LiftStatus status;
    private SortedSet<Integer> floorRequestsGoingUp = new TreeSet<>();
    private SortedSet<Integer> flooRequestsGoingDown = new TreeSet<>(Comparator.<Integer>reverseOrder());

    public Lift(int id, int minFloorNumber) {
        this.id = id;
        currentFloor = minFloorNumber;
        status = LiftStatus.IDLE;
    }

    @Override
    public void notifyObservers(ILiftObserver liftObserver) {
        //update liftObserver
    }

    @Override
    public void noticeChange() {

    }

    public void updateFloorRequests(int floor) {

        switch (status) {
            case MOVING_UP: {
                if (floor > currentFloor) floorRequestsGoingUp.add(floor);
                else flooRequestsGoingDown.add(floor);
                break;
            }
            case MOVING_DOWN: {
                if (floor < currentFloor) flooRequestsGoingDown.add(floor);
                else floorRequestsGoingUp.add(floor);
                break;
            }
            case IDLE: {
                if(floor > currentFloor) floorRequestsGoingUp.add(floor);
                else flooRequestsGoingDown.add(floor);
                break;
            }
        }

    }
}
