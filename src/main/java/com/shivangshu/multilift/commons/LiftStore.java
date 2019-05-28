package com.shivangshu.multilift.commons;

import java.util.ArrayList;
import java.util.List;

public class LiftStore {

    private static LiftStore INSTANCE = null;
    private List<Lift> lifts = new ArrayList<>();

    private LiftStore() {
    }

    public void addLiftsToStore(Lift lift) {
        lifts.add(lift);
    }

    public List<Lift> getLifts() {
        return lifts;
    }

    public static LiftStore getInstance() {
        if (INSTANCE == null) {
            INSTANCE =  new LiftStore();
            return INSTANCE;
        } else return INSTANCE;
    }
}
