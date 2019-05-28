package com.shivangshu.multilift;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftDisplayStore;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.service.LiftDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class MultiLiftSystem {

    static Logger log = LoggerFactory.getLogger(MultiLiftSystem.class);

    private static int numberOfLifts = 4;

    private static int minimumFLoorNumber = 0;

    static LiftStore instance = LiftStore.INSTANCE;
    static LiftDisplayStore displayStoreInstane = LiftDisplayStore.INSTANCE;

    private static LiftDisplay createLiftDisplays(int liftId) {
        LiftDisplay liftDisplay = new LiftDisplay(liftId);
        displayStoreInstane.addLiftDisplays(liftDisplay);
        return liftDisplay;
    }

    private static void createLifts(int liftId) {
        Lift lift = new Lift(liftId, minimumFLoorNumber);
        log.info("Lift Created with id {} " + liftId + " current floor {} " + lift.getCurrentFloor()
                + " Status {} " + lift.getStatus().toString());
        instance.addLiftsToStore(lift);
        LiftDisplay liftDisplay = createLiftDisplays(liftId);
        log.info("Lift Display Created with id {} " + liftId);
        lift.addLiftObservers(liftDisplay);
    }

    public static void main(String[] args) {
        SpringApplication.run(MultiLiftSystem.class);
        for (int i = 0; i < numberOfLifts; i++) {
            createLifts(i + 1);
        }
    }
}
