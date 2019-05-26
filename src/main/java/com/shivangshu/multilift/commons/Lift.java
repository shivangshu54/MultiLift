package com.shivangshu.multilift.commons;

import com.shivangshu.multilift.errors.UnknownLiftStatusError;
import com.shivangshu.multilift.service.ILiftObservable;
import com.shivangshu.multilift.service.ILiftObserver;
import lombok.Data;
import lombok.Getter;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;


@Data
public class Lift extends BaseLift implements ILiftObservable {

    private int id;
    int currentFloor;
    LiftStatus status;
    private SortedSet<Integer> floorRequestsGoingUp = new TreeSet<>();
    private SortedSet<Integer> flooRequestsGoingDown = new TreeSet<>(Comparator.<Integer>reverseOrder());
    private ILiftObserver liftObserver;

    public Lift(int id, int minFloorNumber) {
        this.id = id;
        currentFloor = minFloorNumber;
        status = LiftStatus.IDLE;
    }

    @Override
    public void notifyObservers(ILiftObserver liftObserver) {
        liftObserver.displayInfo();
    }

    @Override
    public void noticeChange() {

    }

    @Override
    public void addLiftObservers(ILiftObserver liftObserver) {
        this.liftObserver = liftObserver;
    }

    public int getDistanceToRequestedFloor(int requestedFloor) {

        switch (status) {
            case MOVING_UP: {
                if (requestedFloor >= this.currentFloor) {
                    return Math.abs(requestedFloor - this.currentFloor);
                } else return Math.abs((this.floorRequestsGoingUp.last() - this.currentFloor) +
                        (this.floorRequestsGoingUp.last() - requestedFloor));
            }

            case MOVING_DOWN: {
                if (requestedFloor <= this.currentFloor) {
                    return Math.abs(this.currentFloor - requestedFloor);
                } else {
                    return Math.abs((this.currentFloor - this.flooRequestsGoingDown.last()) +
                            (requestedFloor - this.flooRequestsGoingDown.last()));
                }
            }
            case IDLE: {
                return Math.abs(requestedFloor - this.currentFloor);
            }
            case ERROR_NONE: {
                //some logging
                return -1;
            }
        }
        return 0;
    }

    /**
     * @param floor this is assumed to be a call made once the sensors at the lift detects and updates to a new floor
     */
    @Override
    public void updateCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    /**
     * @param direction this is assumed to be a call made once the lift status is changed from MOVING_UP, MOVING_DOWN or IDLE.
     *                  The decisioning is on the sensors attached to the lift.
     */
    @Override
    public void updateDirection(String direction) throws UnknownLiftStatusError {
        if (direction.equalsIgnoreCase("UP"))
            this.status = LiftStatus.MOVING_UP;
        else if(direction.equalsIgnoreCase("DOWN"))
            this.status = LiftStatus.MOVING_DOWN;
        else if(direction.equalsIgnoreCase("IDLE"))
            this.status = LiftStatus.IDLE;
        else throw new UnknownLiftStatusError("Unknown Status Update Request Received");
    }
}
