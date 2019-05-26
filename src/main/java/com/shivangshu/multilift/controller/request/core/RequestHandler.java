package com.shivangshu.multilift.controller.request.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import com.shivangshu.multilift.controller.request.InternalRequest;
import com.shivangshu.multilift.service.LiftMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@RestController
public class RequestHandler {


    private BlockingQueue<InternalRequest> internalRequests = new LinkedBlockingQueue<InternalRequest>();
    Gson gson = new Gson();

    @Autowired
    LiftMain liftMain;

    @RequestMapping(value = "/lift/externalRequest", method = RequestMethod.PUT)
    public void addExternalRequests(@RequestBody String payload) {
        ExternalRequest externalRequest = gson.fromJson(payload, ExternalRequest.class);
            liftMain.addExternalRequests(externalRequest);
    }

    @RequestMapping(value = "/lift/internalRequest", method = RequestMethod.PUT)
    public void addInternalRequest(@RequestBody String payload) {
        InternalRequest internalRequest = gson.fromJson(payload, InternalRequest.class);
            liftMain.addInternalRequests(internalRequest);
    }

    @RequestMapping(value = "/lift/floorchange", method = RequestMethod.POST)
    public void updateLiftFloorChange(@RequestBody String payload) {
        JsonObject floorChangeObject = new JsonParser().parse(payload).getAsJsonObject();
        String id = floorChangeObject.get("id").getAsString();
        String floor = floorChangeObject.get("floor").getAsString();
        liftMain.updateLiftFloorChange(id, floor);

    }

    @RequestMapping(value = "/lift/directionchange", method = RequestMethod.POST)
    public void updateLiftDirection(@RequestBody String payload) {
        JsonObject directionChangeObject = new JsonParser().parse(payload).getAsJsonObject();
        String id = directionChangeObject.get("id").getAsString();
        String direction = directionChangeObject.get("direction").getAsString();
        liftMain.updateLiftDirection(id, direction);
    }

//    public ExternalRequest getExternalRequests() {
//        try {
//            return externalRequests.poll(30, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return null;
//        }
    }
