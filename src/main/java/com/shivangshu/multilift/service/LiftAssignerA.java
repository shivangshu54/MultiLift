package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class LiftAssignerA extends BaseLiftAssigner implements ILiftAssigner {

    Logger log = LoggerFactory.getLogger(LiftAssignerA.class);

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
}
