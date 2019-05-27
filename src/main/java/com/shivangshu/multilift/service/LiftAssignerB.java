package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.RequestedDirection;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LiftAssignerB extends BaseLiftAssigner implements ILiftAssigner{

    Logger log = LoggerFactory.getLogger(LiftAssignerB.class);

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
                int minimumTime = Integer.MAX_VALUE;
                for (Lift lift : getLiftsAboveAndMovingDown()) {
                    if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN) <= minimumTime) {
                        minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN);
                        liftToAssign = lift;
                    }
                }
                return liftToAssign;
            } else if (!getIdleLifts().isEmpty()) {
                int minimumTime = Integer.MAX_VALUE;
                for (Lift lift : getIdleLifts()) {
                    if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN) <= minimumTime) {
                        minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN);
                        liftToAssign = lift;
                    }
                }
            } else if (!getLiftsBelowAndMovingUp().isEmpty() || !getLiftsAboveAndMovingUp().isEmpty()) {
                int minimumTime = Integer.MAX_VALUE;
                for (Lift lift : getLiftsBelowAndMovingUp()) {
                    if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN) <= minimumTime) {
                        minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN);
                        liftToAssign = lift;
                    }
                }
                for (Lift lift : getLiftsAboveAndMovingUp()) {
                    if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN) <= minimumTime) {
                        minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN);
                        liftToAssign = lift;
                    }
                }
            } else {
                int minimumTime = Integer.MAX_VALUE;
                for (Lift lift : getLiftsBelowAndMovingDown()) {
                    if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN) <= minimumTime) {
                        minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.DOWN);
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
            int minimumTime = Integer.MAX_VALUE;
            for (Lift lift : getLiftsBelowAndMovingUp()) {
                if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP) <= minimumTime) {
                    minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP);
                    liftToAssign = lift;
                }
            }
        } else if (!getIdleLifts().isEmpty()) {
            int minimumTime = Integer.MAX_VALUE;
            for (Lift lift : getIdleLifts()) {
                if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP) <= minimumTime) {
                    minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP);
                    liftToAssign = lift;
                }
            }
        } else if (!getLiftsAboveAndMovingDown().isEmpty() || !getLiftsBelowAndMovingDown().isEmpty()) {
            int minimumTime = Integer.MAX_VALUE;
            for (Lift lift : getLiftsAboveAndMovingDown()) {
                if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP) <= minimumTime) {
                    minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP);
                    liftToAssign = lift;
                }
            }
            for (Lift lift : getLiftsBelowAndMovingDown()) {
                if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP) <= minimumTime) {
                    minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP);
                    liftToAssign = lift;
                }
            }
        } else {
            int minimumTime = Integer.MAX_VALUE;
            for (Lift lift : getLiftsAboveAndMovingUp()) {
                if (lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP) <= minimumTime) {
                    minimumTime = lift.getTimeToRequestedFloor(requestFromFloor, RequestedDirection.UP);
                    liftToAssign = lift;
                }
            }
        }
        return liftToAssign;
    }
}
