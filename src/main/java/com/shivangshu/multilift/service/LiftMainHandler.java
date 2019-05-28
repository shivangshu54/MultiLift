package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import com.shivangshu.multilift.controller.request.InternalRequest;
import com.shivangshu.multilift.errors.LiftNotFoundException;
import com.shivangshu.multilift.errors.UnknownLiftStatusError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LiftMainHandler {

    @Autowired
    LiftAssignerA liftAssigner;

    @Autowired
    private Environment env;

    Logger log = LoggerFactory.getLogger(LiftMainHandler.class);

    private BlockingQueue<ExternalRequest> externalRequests = new LinkedBlockingQueue<ExternalRequest>();
    private LiftStore liftStoreInstance = LiftStore.getInstance();
    List<Lift> totalLifts = liftStoreInstance.getLifts();

    /**
     * @param r ExternalRequest
     * @throws InterruptedException
     * @throws NullPointerException this method adds the external request to the request queue and calls the allocateLiftToExternalRequest()
     *                              function to allocate a lift based on the algorithm to one particular lift
     */
    public void addExternalRequests(ExternalRequest r) throws InterruptedException, NullPointerException {
        try {
            externalRequests.put(r);
            log.debug("External request from Floor {} " + r.getFromFloor() + " added to queue");
            allocateLiftToExternalRequests();
        } catch (NullPointerException | InterruptedException e) {
            log.error("Exception while adding external Request {}", e.getMessage());
            throw e;
        }
    }

    /**
     * @param r InternalRequest
     * @throws LiftNotFoundException
     * @throws NullPointerException  this method adds internal requests to the corresponding lift's internal request queue.
     */
    public void addInternalRequests(InternalRequest r) throws LiftNotFoundException, NullPointerException {
        try {
            Lift liftToAssignInternalRequest = getLiftById(r.getLiftId());
            liftAssigner.assignInternalRequests(liftToAssignInternalRequest, r.getToFloor());
            log.debug("Lift id {} " + liftToAssignInternalRequest.getId() + " to process Requests while going up {}" +
                    liftToAssignInternalRequest.getFloorRequestsGoingUp());
            log.debug("Lift {}" + liftToAssignInternalRequest.getId() + " to process Requests while going down {}" +
                    liftToAssignInternalRequest.getFlooRequestsGoingDown());

        } catch (LiftNotFoundException | NullPointerException e) {
            log.error("Exception while adding internal Request {}", e.getMessage());
            throw e;
        }
    }

    /**
     * @param id
     * @return
     * @throws LiftNotFoundException this method returns a lift object based on the requested Id.
     */
    private Lift getLiftById(int id) throws LiftNotFoundException {
        for (Lift lift : totalLifts) {
            if (id == lift.getId()) {
                return lift;
            }
        }
        log.error("Lift with id {} " + id + "not found");
        throw new LiftNotFoundException("Lift with Id {} " + id + " does not exists");
    }

    /**
     * @param id
     * @param floor
     * @throws LiftNotFoundException this method is called by sensory changes when lift moves to a different floor. It updates the lift objects
     *                               currentFloor method to the corresponding floor detected by the sensor.
     */
    public void updateLiftFloorChange(String id, String floor) throws LiftNotFoundException {
        for (Lift l : totalLifts) {
            if (l.getId() == Integer.valueOf(id)) {
                l.updateCurrentFloor(Integer.valueOf(floor));
                return;
            }
        }
        throw new LiftNotFoundException("Lift with Id {} " + id + " does not exists");
    }

    /**
     * @param id
     * @param direction
     * @throws UnknownLiftStatusError this method is called by sensory changes when lift moves to a different direction from previous. It updates the lift objects
     *                                status variable to the corresponding direction of movement as detected by the sensor.
     */
    public void updateLiftDirection(String id, String direction) throws UnknownLiftStatusError {
        for (Lift lift : totalLifts) {
            if (lift.getId() == Integer.valueOf(id)) {
                lift.updateDirection(direction);
            }
        }
    }

    /**
     * @throws InterruptedException Allocates a lift from the pool of lifts to the external requests queue one by one. It calls the ILiftAssigner's assignLift()
     *                              method to assign a lift based on the algorithm implemented.
     */
    public void allocateLiftToExternalRequests() throws InterruptedException {
        try {
            while (true) {
                if (externalRequests.isEmpty()) break;
                ExternalRequest request = externalRequests.take();
                Lift lift = liftAssigner.assignLift(request);
                log.info("External Request from Floor {} " + request.getFromFloor() + " added to Lift id {} " + lift.getId());
                liftAssigner.assignInternalRequests(lift, request.getFromFloor());
            }
        } catch (InterruptedException e) {
            log.error("Unable to assign Request to lift");
            throw e;
        }
    }

    public void disableRequestsToLiftWithMaxWeight(int liftId) throws LiftNotFoundException {
        Lift lift = getLiftById(liftId);
        lift.setHasReachedMaxWeight(true);
        log.info("Disabling all further requests to lift {} "+ liftId + " as max weight has reached");
    }

    public void enableRequestsToLift(Integer liftId) throws LiftNotFoundException {
        Lift lift = getLiftById(liftId);
        lift.setHasReachedMaxWeight(false);
        log.info("Enabling requests to lift {} "+liftId + " as weight has reduced from max weight");
    }

    /**
     * It sends all lifts to the ground floor and is triggered in case of power failure
     */
    public void resetAllLifts() {
        for (Lift lift : totalLifts) {
            if (lift.getCurrentFloor() != Integer.valueOf(env.getProperty("config.minimumFloorNumber"))) {
                lift.getFloorRequestsGoingUp().clear();
                lift.getFlooRequestsGoingDown().clear();
                lift.setHasReachedMaxWeight(false);
                liftAssigner.assignInternalRequests(lift, Integer.valueOf(env.getProperty("config.minimumFloorNumber")));
                log.info("Lift id {} " + lift.getId() + " is going to base floor {} " + Integer.valueOf(env.getProperty("config.minimumFloorNumber")));
            }
        }
    }
}
