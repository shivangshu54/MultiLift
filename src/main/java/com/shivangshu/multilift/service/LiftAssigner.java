package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStatus;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.commons.RequestedDirection;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LiftAssigner {

    Logger log = LoggerFactory.getLogger(LiftAssigner.class);


    private static final List<Lift> liftsInService = LiftStore.INSTANCE.getLifts();
    private static List<Lift> liftsBelowRequestedFloor = new ArrayList<>();
    private static List<Lift> liftsAboveRequestedFloor = new ArrayList<>();
    private static List<Lift> liftsAtSameFloor = new ArrayList<>();
    private static List<Lift> idleLifts = new ArrayList<>();

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

    private List<Lift> getIdleLifts() {
        for (Lift lift : liftsInService) {
            if(lift.getStatus() == LiftStatus.IDLE) {
                idleLifts.add(lift);
            }
        }
        return idleLifts;
    }

    public Lift assignLift(ExternalRequest request) {
        updateLiftsAboveRequestedFloor(request.getFromFloor());
        updateLiftsBelowRequestedFloor(request.getFromFloor());
        updateLiftsAtSameFloor(request.getFromFloor());
        log.debug("Lifts Above Requested Floor for External Request are {}" + liftsAboveRequestedFloor.size() );
        log.debug("Lifts Below Requested Floor for External Request are {}" + liftsBelowRequestedFloor.size() );
        log.debug("Lifts At Same Requested Floor for External Request are {}" + liftsAtSameFloor.size() );

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
    }

}
