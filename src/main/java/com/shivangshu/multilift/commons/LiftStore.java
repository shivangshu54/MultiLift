package com.shivangshu.multilift.commons;

import java.util.ArrayList;
import java.util.List;

public class LiftStore {

    public static final LiftStore INSTANCE = new LiftStore();
    private List<Lift> lifts = new ArrayList<>();

    private LiftStore(){}

    public void addLiftsToStore(Lift lift) {
        lifts.add(lift);
    }

    public List<Lift> getLifts() {
        return lifts;
    }
}
