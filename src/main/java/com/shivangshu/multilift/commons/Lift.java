package com.shivangshu.multilift.commons;

import com.shivangshu.multilift.errors.UnknownLiftStatusError;
import com.shivangshu.multilift.service.ILiftObservable;
import com.shivangshu.multilift.service.ILiftObserver;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;


@Data
public class Lift extends BaseLift implements ILiftObservable {

    Logger log = LoggerFactory.getLogger(Lift.class);

    private int id;
    int currentFloor;
    LiftStatus status;
    private SortedSet<Integer> floorRequestsGoingUp = new TreeSet<>();
    private SortedSet<Integer> flooRequestsGoingDown = new TreeSet<>(Comparator.<Integer>reverseOrder());
    private ILiftObserver liftObserver;
    private boolean liftInProcess = false;
    private boolean hasReachedMaxWeight;

    public Lift(int id, int minFloorNumber) {
        this.id = id;
        currentFloor = minFloorNumber;
        status = LiftStatus.IDLE;
        hasReachedMaxWeight = false;
    }

    @Override
    public void notifyObservers() {
        liftObserver.displayInfo(this);
    }

    @Override
    public void addLiftObservers(ILiftObserver liftObserver) {
        this.liftObserver = liftObserver;
    }

    /**
     *
     * @param requestedFloor
     * @return
     * return the minimum distance the lift has to travel to reach to the requested floor. The minimum distance
     * is calculated based on the total internal requests present in the lift's internal request queue.
     */
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
                log.error("Unable to get Distance to requested Floor {} " + requestedFloor + " for Lift id {} " + this.id);
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
        notifyObservers();
    }

    /**
     * @param direction this is assumed to be a call made once the lift status is changed from MOVING_UP, MOVING_DOWN or IDLE.
     *                  The decisioning is on the sensors attached to the lift.
     */
    @Override
    public void updateDirection(String direction) throws UnknownLiftStatusError {
        if (direction.equalsIgnoreCase("UP"))
            this.status = LiftStatus.MOVING_UP;
        else if (direction.equalsIgnoreCase("DOWN"))
            this.status = LiftStatus.MOVING_DOWN;
        else if (direction.equalsIgnoreCase("IDLE"))
            this.status = LiftStatus.IDLE;
        else throw new UnknownLiftStatusError("Unknown Status Update Request Received");
        notifyObservers();
    }

    /**
     * perform the internal requests assigned to each lifts queues. This is actually a call to the lift motor to take the
     * lift to the assigned floors in the internal queue request
     */
    public void performInternalRequests() {
        while (true) {
            liftInProcess = true;
            if (!floorRequestsGoingUp.isEmpty()) {
                for (Iterator i = floorRequestsGoingUp.iterator(); i.hasNext(); ) {
                    Integer floorServing = (Integer)i.next();
                    log.info("Lift Id {} " + this.id + " is going up serving request to floor {} " + floorServing);
                    floorRequestsGoingUp.remove(new Integer(floorServing));
                    //It will be a call to the lift motor to start moving to the requested floor
                }
            } else if(!flooRequestsGoingDown.isEmpty()) {
                for (Iterator i = flooRequestsGoingDown.iterator(); i.hasNext(); ) {
                    Integer floorServing = (Integer) i.next();
                    log.info("Lift Id {} " + this.id + " is going down serving request to floor {} " + floorServing);
                    flooRequestsGoingDown.remove(new Integer(floorServing));
                }
            }
            if (flooRequestsGoingDown.isEmpty() && floorRequestsGoingUp.isEmpty()) {
                liftInProcess = false;
                break;
            }
            else continue;
        }
    }
}

