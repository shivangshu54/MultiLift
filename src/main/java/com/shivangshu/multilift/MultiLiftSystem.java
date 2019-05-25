package com.shivangshu.multilift;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.commons.LiftStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiLiftSystem
{

    @Value("${numberOfLifts}")
    private static int numberOfLifts;

    @Value("${minimumFloorNumber}")
    private static int minimumFLoorNumber;

    static LiftStore instance = LiftStore.INSTANCE;

    public static void main(String[] args) {
        for(int i = 0; i< numberOfLifts; i++) {
            createLifts(i+1);
        }
        SpringApplication.run(MultiLiftSystem.class);

    }

    private static void createLifts(int liftId) {
        Lift lift = new Lift(liftId, minimumFLoorNumber);
        instance.addLiftsToStore(lift);
    }
}
