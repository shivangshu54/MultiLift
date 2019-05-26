package com.shivangshu.multilift;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftDisplayStore;
import com.shivangshu.multilift.commons.LiftStore;
import com.shivangshu.multilift.service.LiftDisplay;
import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MultiLiftSystem {

    @Value( "${config.numberOfLifts}" )
    private static int numberOfLifts = 4;

    @Value("${config.minimumFloorNumber}")
    private static int minimumFLoorNumber = 0;

    @Autowired
    private static Environment env;

    static LiftStore instance = LiftStore.INSTANCE;
    static LiftDisplayStore displayStoreInstane = LiftDisplayStore.INSTANCE;

    public static void main(String[] args) {
        SpringApplication.run(MultiLiftSystem.class);
        for (int i = 0; i < numberOfLifts; i++) {
            createLifts(i + 1);
        }
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
