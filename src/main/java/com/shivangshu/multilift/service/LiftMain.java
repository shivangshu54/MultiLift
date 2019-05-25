package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.controller.request.ExternalRequest;
import com.shivangshu.multilift.controller.request.InternalRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class LiftMain {

    private BlockingQueue<ExternalRequest> externalRequests = new LinkedBlockingQueue<ExternalRequest>();
    private LiftStore liftStoreInstance = LiftStore.INSTANCE;

    public void addExternalRequests(ExternalRequest r) {
        try {
            externalRequests.put(r);
        } catch (NullPointerException | InterruptedException e) {
            //some logging
        }
    }

    public void addInternalRequests(InternalRequest r) {

        try{

        }catch (NullPointerException | InterruptedException e) {
            //some logging
        }
    }
}
