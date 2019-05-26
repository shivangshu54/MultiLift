package com.shivangshu.multilift;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftDisplayStore;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.service.LiftDisplay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiLiftSystem {

    @Value("${numberOfLifts}")
    private static int numberOfLifts;

    @Value("${minimumFloorNumber}")
    private static int minimumFLoorNumber;

    static LiftStore instance = LiftStore.INSTANCE;
    static LiftDisplayStore displayStoreInstane = LiftDisplayStore.INSTANCE;

    public static void main(String[] args) {
        for (int i = 0; i < numberOfLifts; i++) {
            createLifts(i + 1);
        }
        SpringApplication.run(MultiLiftSystem.class);

    }

    private static LiftDisplay createLiftDisplays(int liftId) {
        LiftDisplay liftDisplay = new LiftDisplay(liftId);
        displayStoreInstane.addLiftDisplays(liftDisplay);
        return liftDisplay;
    }

    private static void createLifts(int liftId) {
        Lift lift = new Lift(liftId, minimumFLoorNumber);
        instance.addLiftsToStore(lift);
        LiftDisplay liftDisplay = createLiftDisplays(liftId);
        lift.addLiftObservers(liftDisplay);

    }
}
