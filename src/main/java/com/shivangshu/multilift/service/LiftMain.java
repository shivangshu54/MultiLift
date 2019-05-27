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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LiftMain {

    @Autowired
    LiftAssignerA liftAssigner;

    Logger log = LoggerFactory.getLogger(LiftMain.class);

    private BlockingQueue<ExternalRequest> externalRequests = new LinkedBlockingQueue<ExternalRequest>();
    private LiftStore liftStoreInstance = LiftStore.INSTANCE;
    List<Lift> totalLifts = liftStoreInstance.getLifts();

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

    private Lift getLiftById(int id) throws LiftNotFoundException {
        for (Lift lift : totalLifts) {
            if (id == lift.getId()) {
                return lift;
            }
        }
        log.error("Lift with id {} " + id + "not found");
        throw new LiftNotFoundException("Lift with Id {} " + id + " does not exists");
    }

    public void updateLiftFloorChange(String id, String floor) throws LiftNotFoundException {
        for (Lift l : totalLifts) {
            if (l.getId() == Integer.valueOf(id)) {
                l.updateCurrentFloor(Integer.valueOf(floor));
                break;
            }
        }
        throw new LiftNotFoundException("Lift with Id {} " + id + " does not exists");
    }

    public void updateLiftDirection(String id, String direction) throws UnknownLiftStatusError {
        for (Lift lift : totalLifts) {
            if (lift.getId() == Integer.valueOf(id)) {
                lift.updateDirection(direction);
            }
        }
    }

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
}
