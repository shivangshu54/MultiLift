package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.commons.LiftStore;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseLiftAssigner {

    protected static final List<Lift> liftsInService = LiftStore.getInstance().getLifts();

    /**
     *  the three lists of Lifts below store only the lifts for which max weight has not reached
     */
    protected static List<Lift> liftsBelowRequestedFloor = new ArrayList<>();
    protected static List<Lift> liftsAboveRequestedFloor = new ArrayList<>();
    protected static List<Lift> liftsAtSameFloor = new ArrayList<>();

    protected static List<Lift> idleLifts = new ArrayList<>();

    protected void updateLiftsAboveRequestedFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() > requestFromFloor && !lift.isHasReachedMaxWeight()) {
                liftsAboveRequestedFloor.add(lift);
            }
        }
    }

    protected void updateLiftsBelowRequestedFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() < requestFromFloor && !lift.isHasReachedMaxWeight()) {
                liftsBelowRequestedFloor.add(lift);
            }
        }
    }

    protected void updateLiftsAtSameFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() == requestFromFloor && !lift.isHasReachedMaxWeight()) {
                liftsAtSameFloor.add(lift);
            }
        }
    }

    protected List<Lift> getLiftsAboveAndMovingDown() {
        List<Lift> liftsAboveMovingDown = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_DOWN && !lift.isHasReachedMaxWeight()) {
                liftsAboveMovingDown.add(lift);
            }
        }
        return liftsAboveMovingDown;
    }

    protected List<Lift> getLiftsAboveAndMovingUp() {
        List<Lift> liftsAboveMovingUp = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP && !lift.isHasReachedMaxWeight()) {
                liftsAboveMovingUp.add(lift);
            }
        }
        return liftsAboveMovingUp;
    }

    protected List<Lift> getLiftsSameFloorAndIdle() {
        List<Lift> liftsSameFloorIdle = new ArrayList<>();
        for (Lift lift : liftsAtSameFloor) {
            if (lift.getStatus() == LiftStatus.IDLE) {
                liftsSameFloorIdle.add(lift);
            }
        }
        return liftsSameFloorIdle;
    }

    protected List<Lift> getLiftsBelowAndMovingDown() {
        List<Lift> liftsBelowAndMovingDown = new ArrayList<>();
        for (Lift lift : liftsBelowRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_DOWN && !lift.isHasReachedMaxWeight()) {
                liftsBelowAndMovingDown.add(lift);
            }
        }
        return liftsBelowAndMovingDown;
    }

    protected List<Lift> getLiftsBelowAndMovingUp() {
        List<Lift> liftsBelowAndMovingUp = new ArrayList<>();
        for (Lift lift : liftsBelowRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP && !lift.isHasReachedMaxWeight()) {
                liftsBelowAndMovingUp.add(lift);
            }
        }
        return liftsBelowAndMovingUp;
    }

    protected List<Lift> getIdleLifts() {
        for (Lift lift : liftsInService) {
            if (lift.getStatus() == LiftStatus.IDLE) {
                idleLifts.add(lift);
            }
        }
        return idleLifts;
    }

    /**
     *
     * @param lift
     * @param toFloorRequest
     * this assigns internal requests when user has onboarded the lift based on priority queue algorithm
     */
    public void assignInternalRequests(Lift lift, int toFloorRequest) {

        switch (lift.getStatus()) {
            case MOVING_UP: {
                if (toFloorRequest > lift.getCurrentFloor()) lift.getFloorRequestsGoingUp().add(toFloorRequest);
                else lift.getFlooRequestsGoingDown().add(toFloorRequest);
                break;
            }
            case MOVING_DOWN: {
                if (toFloorRequest < lift.getCurrentFloor()) lift.getFlooRequestsGoingDown().add(toFloorRequest);
                else lift.getFloorRequestsGoingUp().add(toFloorRequest);
                break;
            }
            case IDLE: {
                if (toFloorRequest > lift.getCurrentFloor()) lift.getFloorRequestsGoingUp().add(toFloorRequest);
                else lift.getFlooRequestsGoingDown().add(toFloorRequest);
                break;
            }
        }
        // this is to be uncommented to test the flow. Ideally it should run always and sensors should continuously call updateFloor
        // and updateDirection function for this to function seamlessly. But given the restriction, we have to manually hit the API's
        // and run the flow.
        if (!lift.isLiftInProcess())
            lift.performInternalRequests();
    }

}
