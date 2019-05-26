package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftPosition;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.commons.LiftStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiftAssigner {

    private static final List<Lift> liftsInService = LiftStore.INSTANCE.getLifts();
    private static final int TOTAL_LIFTS = liftsInService.size();
    private static int idleLifts = 0;
    private static List<Lift> liftsBelowRequestedFloor = new ArrayList<>();
    private static List<Lift> liftsAboveRequestedFloor = new ArrayList<>();
    private static List<Lift> liftsAtSameFloor = new ArrayList<>();

    private void updateLiftsAboveRequestedFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() > requestFromFloor) {
                liftsAboveRequestedFloor.add(lift);
            }
        }
    }

    private void updateLiftsBelowRequestedFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() < requestFromFloor) {
                liftsBelowRequestedFloor.add(lift);
            }
        }
    }

    private void updateLiftsAtSameFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() == requestFromFloor) {
                liftsAtSameFloor.add(lift);
            }
        }
    }

    private List<Lift> getLiftsAboveAndMovingDown() {
        List<Lift> liftsAboveMovingDown = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_DOWN) {
                liftsAboveMovingDown.add(lift);
            }
        }
        return liftsAboveMovingDown;
    }

    private List<Lift> getLiftsAboveAndMovingUp() {
        List<Lift> liftsAboveMovingUp = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP) {
                liftsAboveMovingUp.add(lift);
            }
        }
        return liftsAboveMovingUp;
    }

    private List<Lift> getLiftsSameFloorAndIdle() {
        List<Lift> liftsSameFloorIdle = new ArrayList<>();
        for (Lift lift : liftsAtSameFloor) {
            if (lift.getStatus() == LiftStatus.IDLE) {
                liftsSameFloorIdle.add(lift);
            }
        }
        return liftsSameFloorIdle;
    }

    private List<Lift> getLiftsBelowAndMovingDown() {
        List<Lift> liftsBelowAndMovingDown = new ArrayList<>();
        for (Lift lift : liftsBelowRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_DOWN) {
                liftsBelowAndMovingDown.add(lift);
            }
        }
        return liftsBelowAndMovingDown;
    }

    private List<Lift> getLiftsBelowAndMovingUp() {
        List<Lift> liftsBelowAndMovingUp = new ArrayList<>();
        for (Lift lift : liftsBelowRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP) {
                liftsBelowAndMovingUp.add(lift);
            }
        }
        return liftsBelowAndMovingUp;
    }

    private int getDistanceToRequestedFloor(int requestedFloor, Lift lift) {
        switch (lift.getStatus()) {
            case LiftStatus.MOVING_UP: {
                if (requestedFloor >= lift.getCurrentFloor()) {
                    return Math.abs(requestedFloor - lift.getCurrentFloor());
                } else return Math.abs((lift.getFloorRequestsGoingUp().last() - lift.getCurrentFloor()) +
                        (lift.getFloorRequestsGoingUp().last() - requestedFloor));
            }

            case LiftStatus.MOVING_DOWN: {
                if (requestedFloor <= lift.getCurrentFloor()) {
                    return Math.abs(lift.getCurrentFloor() - requestedFloor);
                } else {
                    return Math.abs((lift.getCurrentFloor() - lift.getFloorRequestGoingDown().last()) +
                            (requestedFloor - lift.getFloorRequestGoingDown().last()));
                }
            }
            case LiftStatus.IDLE: {
                return Math.abs(requestedFloor - lift.getCurrentFloor());
            }

        }
    }

    private Lift assignLift() {

    }
}
