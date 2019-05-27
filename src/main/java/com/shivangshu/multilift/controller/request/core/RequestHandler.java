package com.shivangshu.multilift.controller.request.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shivangshu.multilift.commons.RequestedDirection;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import com.shivangshu.multilift.controller.request.InternalRequest;
import com.shivangshu.multilift.errors.UnknownLiftStatusError;
import com.shivangshu.multilift.service.LiftMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RestController
public class RequestHandler {

    Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private BlockingQueue<InternalRequest> internalRequests = new LinkedBlockingQueue<InternalRequest>();
    Gson gson = new Gson();

    @Autowired
    LiftMain liftMain;

    /**
     * @param payload format -> {"fromFloor": 3, "requestedDirection": "DOWN"}
     *                API will be called by sensor whenever an external button to request a lift has been pressed
     *                Allowed requestedDirections -> {"DOWN", "UP"}
     */
    @RequestMapping(value = "/lift/externalRequest", method = RequestMethod.PUT)
    public void addExternalRequests(@RequestBody String payload) {
        ExternalRequest externalRequest = new ExternalRequest();
        JsonObject externalRequestJson = new JsonParser().parse(payload).getAsJsonObject();
        String requestedDirection = externalRequestJson.get("requestedDirection").getAsString();
        if (requestedDirection.equalsIgnoreCase("DOWN"))
            externalRequest.setRequestedDirection(RequestedDirection.DOWN);
        else if (requestedDirection.equalsIgnoreCase("UP"))
            externalRequest.setRequestedDirection(RequestedDirection.UP);
        externalRequest.setFromFloor(externalRequestJson.get("fromFloor").getAsInt());
        logger.info("External Request received {} "+ externalRequest);
        liftMain.addExternalRequests(externalRequest);
    }

    /**
     * @param payload format -> {"toFloor": 4, "liftId": 1}
     *                API will be called by sensor whenever an internal button from a moving lift has been pressed.
     */
    @RequestMapping(value = "/lift/internalRequest", method = RequestMethod.PUT)
    public void addInternalRequest(@RequestBody String payload) {
        InternalRequest internalRequest = gson.fromJson(payload, InternalRequest.class);
        logger.info("Internal Request Received {} "+ internalRequest);
        liftMain.addInternalRequests(internalRequest);
    }

    /**
     * @param payload format -> {"id": 1, "floor": 3}
     *                This will be called by a senor when it detects a change in lift floor.
     */
    @RequestMapping(value = "/lift/floorchange", method = RequestMethod.POST)
    public void updateLiftFloorChange(@RequestBody String payload) {
        JsonObject floorChangeObject = new JsonParser().parse(payload).getAsJsonObject();
        String id = floorChangeObject.get("id").getAsString();
        String floor = floorChangeObject.get("floor").getAsString();
        logger.debug("Floor Change Request Received : Lift Id {} "+id+" Floor {} "+floor);
        liftMain.updateLiftFloorChange(id, floor);

    }

    /**
     * @param payload format -> {"id": 1, "direction": "UP"}
     *                This will be called by a senor when it detects a change in lift direction.
     *                Allowed direction values {"DOWN", "UP","IDLE"}
     */
    @RequestMapping(value = "/lift/directionchange", method = RequestMethod.POST)
    public void updateLiftDirection(@RequestBody String payload) {
        JsonObject directionChangeObject = new JsonParser().parse(payload).getAsJsonObject();
        String id = directionChangeObject.get("id").getAsString();
        String direction = directionChangeObject.get("direction").getAsString();
        logger.debug("Lift direction change request Received : Lift Id {} "+id+" Direction {} "+direction);

        try {
            liftMain.updateLiftDirection(id, direction);
        } catch (UnknownLiftStatusError unknownLiftStatusError) {
            unknownLiftStatusError.printStackTrace();
        }
    }
}
