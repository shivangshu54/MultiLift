package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LiftAssignerA implements ILiftAssigner {

    Logger log = LoggerFactory.getLogger(LiftAssignerA.class);


    private static final List<Lift> liftsInService = LiftStore.INSTANCE.getLifts();

    /**
     *  the three lists of Lifts below store only the lifts for which max weight has not reached
     */
    private static List<Lift> liftsBelowRequestedFloor = new ArrayList<>();
    private static List<Lift> liftsAboveRequestedFloor = new ArrayList<>();
    private static List<Lift> liftsAtSameFloor = new ArrayList<>();

    private static List<Lift> idleLifts = new ArrayList<>();

    private void updateLiftsAboveRequestedFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() > requestFromFloor && !lift.isHasReachedMaxWeight()) {
                liftsAboveRequestedFloor.add(lift);
            }
        }
    }

    private void updateLiftsBelowRequestedFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() < requestFromFloor && !lift.isHasReachedMaxWeight()) {
                liftsBelowRequestedFloor.add(lift);
            }
        }
    }

    private void updateLiftsAtSameFloor(int requestFromFloor) {
        for (Lift lift : liftsInService) {
            if (lift.getCurrentFloor() == requestFromFloor && !lift.isHasReachedMaxWeight()) {
                liftsAtSameFloor.add(lift);
            }
        }
    }

    private List<Lift> getLiftsAboveAndMovingDown() {
        List<Lift> liftsAboveMovingDown = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_DOWN && !lift.isHasReachedMaxWeight()) {
                liftsAboveMovingDown.add(lift);
            }
        }
        return liftsAboveMovingDown;
    }

    private List<Lift> getLiftsAboveAndMovingUp() {
        List<Lift> liftsAboveMovingUp = new ArrayList<>();
        for (Lift lift : liftsAboveRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP && !lift.isHasReachedMaxWeight()) {
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
            if (lift.getStatus() == LiftStatus.MOVING_DOWN && !lift.isHasReachedMaxWeight()) {
                liftsBelowAndMovingDown.add(lift);
            }
        }
        return liftsBelowAndMovingDown;
    }

    private List<Lift> getLiftsBelowAndMovingUp() {
        List<Lift> liftsBelowAndMovingUp = new ArrayList<>();
        for (Lift lift : liftsBelowRequestedFloor) {
            if (lift.getStatus() == LiftStatus.MOVING_UP && !lift.isHasReachedMaxWeight()) {
                liftsBelowAndMovingUp.add(lift);
            }
        }
        return liftsBelowAndMovingUp;
    }

    private List<Lift> getIdleLifts() {
        for (Lift lift : liftsInService) {
            if (lift.getStatus() == LiftStatus.IDLE) {
                idleLifts.add(lift);
            }
        }
        return idleLifts;
    }

    /**
     *
     * @param request
     * @return
     * this is the actual algorithm implementation of assigning a lift to an external request.
     */
    public Lift assignLift(ExternalRequest request) {
        updateLiftsAboveRequestedFloor(request.getFromFloor());
        updateLiftsBelowRequestedFloor(request.getFromFloor());
        updateLiftsAtSameFloor(request.getFromFloor());
        log.debug("Lifts Above Requested Floor for External Request are {}" + liftsAboveRequestedFloor.size());
        log.debug("Lifts Below Requested Floor for External Request are {}" + liftsBelowRequestedFloor.size());
        log.debug("Lifts At Same Requested Floor for External Request are {}" + liftsAtSameFloor.size());

        Lift liftToAssign = null;
        switch (request.getRequestedDirection()) {
            case DOWN: {
                liftToAssign = assignLiftGoingDownRequest(request.getFromFloor());
                break;
            }
            case UP: {
                liftToAssign = assignLiftGoingUpRequest(request.getFromFloor());
                break;
            }
        }
        return liftToAssign;
    }

    /**
     *
     * @param requestFromFloor
     * @return
     * finds the lift to assign when external request is for GOING_DOWN type
     */
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
            } else if (!getLiftsBelowAndMovingUp().isEmpty() || !getLiftsAboveAndMovingUp().isEmpty()) {
                int minimumDistance = Integer.MAX_VALUE;
                for (Lift lift : getLiftsBelowAndMovingUp()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
                for (Lift lift : getLiftsAboveAndMovingUp()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
            } else {
                int minimumDistance = Integer.MAX_VALUE;
                for (Lift lift : getLiftsBelowAndMovingDown()) {
                    if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                        minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                        liftToAssign = lift;
                    }
                }
            }
        }
        return liftToAssign;
    }

    /**
     *
     * @param requestFromFloor
     * @return
     * finds the lift to assign when external request is for GOING_UP type
     */
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
        } else if (!getLiftsAboveAndMovingDown().isEmpty() || !getLiftsBelowAndMovingDown().isEmpty()) {
            int minimumDistance = Integer.MAX_VALUE;
            for (Lift lift : getLiftsAboveAndMovingDown()) {
                if (lift.getDistanceToRequestedFloor(requestFromFloor) <= minimumDistance) {
                    minimumDistance = lift.getDistanceToRequestedFloor(requestFromFloor);
                    liftToAssign = lift;
                }
            }
            for (Lift lift : getLiftsBelowAndMovingDown()) {
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
        if (!lift.isLiftInProcess())
            lift.performInternalRequests();
    }

}
