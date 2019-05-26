package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import com.shivangshu.multilift.controller.request.InternalRequest;
import com.shivangshu.multilift.errors.LiftNotFoundException;
import com.shivangshu.multilift.errors.UnknownLiftStatusError;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LiftMain {

    private BlockingQueue<ExternalRequest> externalRequests = new LinkedBlockingQueue<ExternalRequest>();
    private LiftStore liftStoreInstance = LiftStore.INSTANCE;
    List<Lift> totalLifts = liftStoreInstance.getLifts();

    public void addExternalRequests(ExternalRequest r) {
        try {
            externalRequests.put(r);
        } catch (NullPointerException | InterruptedException e) {
            //some logging
        }
    }

    public void addInternalRequests(InternalRequest r) {
        try {
            Lift liftToAssignInternalRequest = getLiftById(r.getLiftId());
            liftToAssignInternalRequest.updateFloorRequests(r.getToFloor);
        } catch (NullPointerException | LiftNotFoundException e) {
            //some logging
        }
    }

    private Lift getLiftById(int id) throws Exception {
        for (Lift lift : totalLifts) {
            if (id == lift.getId()) {
                return lift;
            }
        }
        throw new LiftNotFoundException("Lift with Id {} " + id + " does not exists");
    }

    public void updateLiftFloorChange(String id, String floor) {
        for(Lift l : totalLifts) {
            if(l.getId() == Integer.valueOf(id)) {
                l.updateCurrentFloor(Integer.valueOf(floor));
            }
        }
    }

    public void updateLiftDirection(String id, String direction) throws UnknownLiftStatusError {
        for(Lift lift : totalLifts) {
            if(lift.getId() == Integer.valueOf(id)) {
                lift.updateDirection(direction);
            }
        }
    }
}
