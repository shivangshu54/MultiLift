package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.commons.RequestedDirection;
import com.shivangshu.multilift.controller.request.ExternalRequest;

import java.util.ArrayList;
import java.util.List;

import static com.shivangshu.multilift.commons.RequestedDirection.DOWN;
import static com.shivangshu.multilift.commons.RequestedDirection.UP;

public class LiftAssigner {

    private static final List<Lift> liftsInService = LiftStore.INSTANCE.getLifts();
    private static final int TOTAL_LIFTS = liftsInService.size();
    private static int idleLifts = TOTAL_LIFTS;
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
        idleLifts -= liftsAboveMovingDown.size();
        return liftsAboveMovingDown;
    }

    private List<Lift> getLiftsAboveAndMovingUp() {
        List<Lift> liftsAboveMovingUp = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP) {
                liftsAboveMovingUp.add(lift);
            }
        }
        idleLifts -= liftsAboveMovingUp.size();
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
        idleLifts -= liftsBelowAndMovingDown.size();
        return liftsBelowAndMovingDown;
    }

    private List<Lift> getLiftsBelowAndMovingUp() {
        List<Lift> liftsBelowAndMovingUp = new ArrayList<>();
        for (Lift lift : liftsBelowRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP) {
                liftsBelowAndMovingUp.add(lift);
            }
        }
        idleLifts -= liftsBelowAndMovingUp.size();
        return liftsBelowAndMovingUp;
    }

    //implement
    private List<Lift> getIdleLifts() {
        List<Lift> idleLifts = new ArrayList<>();
        for (Lift lift :)
    }

    private Lift assignLift(ExternalRequest request) {
        updateLiftsAboveRequestedFloor(request.getFromFloor());
        updateLiftsBelowRequestedFloor(request.getFromFloor());
        updateLiftsAtSameFloor(request.getFromFloor());

        Lift liftToAssign = null;
        switch (request.getRequestedDirection()) {
            case DOWN: {
               liftToAssign =  assignLiftGoingDownRequest(request.getFromFloor());
                break;
            }
            case UP: {
                liftToAssign = assignLiftGoingUpRequest(request.getFromFloor());
                break;
            }
        }
        return liftToAssign;
    }

    private Lift assignLiftGoingDownRequest(int requestFromFloor) {
        Lift liftToAssign = null;
        if (!getLiftsSameFloorAndIdle().isEmpty())
            return getLiftsSameFloorAndIdle().get(0);
        else {
            if (!getLiftsAboveAndMovingDown().isEmpty()) {
                int minimumDistance = Integer.MAX_VALUE;
                for (Lift lift : getLiftsAboveAndMovingDown()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
                return liftToAssign;
            } else if (!getIdleLifts().isEmpty()) {
                int minimumDistance = Integer.MAX_VALUE;
                for (Lift lift : getIdleLifts()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
            } else if (!getLiftsBelowAndMovingUp().isEmpty()) {
                int minimumDistance = Integer.MAX_VALUE;
                for (Lift lift : getLiftsBelowAndMovingUp()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
            } else {
                int minimumDistance = Integer.MAX_VALUE;
                for (Lift lift : getLiftsAboveAndMovingDown()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
            }
        }
        return liftToAssign;
    }

    private Lift assignLiftGoingUpRequest(int requestFromFloor) {
        Lift liftToAssign = null;
        if (!getLiftsSameFloorAndIdle().isEmpty())
            return getLiftsSameFloorAndIdle().get(0);
        if (!getLiftsBelowAndMovingUp().isEmpty()) {
            int minimumDistance = Integer.MAX_VALUE;
            for (Lift lift : getLiftsBelowAndMovingUp()) {
                if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                    minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                    liftToAssign = lift;
                }
            }
        } else if (!getIdleLifts().isEmpty()) {
            int minimumDistance = Integer.MAX_VALUE;
            for (Lift lift : getIdleLifts()) {
                if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                    minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                    liftToAssign = lift;
                }
            }
        } else if (!getLiftsAboveAndMovingDown().isEmpty()) {
            int minimumDistance = Integer.MAX_VALUE;
            for (Lift lift : getLiftsAboveAndMovingDown()) {
                if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                    minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                    liftToAssign = lift;
                }
            }
        } else {
            int minimumDistance = Integer.MAX_VALUE;
            for (Lift lift : getLiftsAboveAndMovingUp()) {
                if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                    minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                    liftToAssign = lift;
                }
            }
        }
        return liftToAssign;
    }
}
