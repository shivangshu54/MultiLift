package com.shivangshu.multilift.commons;

import com.shivangshu.multilift.service.LiftDisplay;

import java.util.ArrayList;
import java.util.List;

public class LiftDisplayStore {
    public static final LiftDisplayStore INSTANCE = new LiftDisplayStore();
    private List<LiftDisplay> liftDisplays = new ArrayList<>();

    private LiftDisplayStore(){}

    public void addLiftDisplays(LiftDisplay liftDisplay) {
        liftDisplays.add(liftDisplay);
    }

    public List<LiftDisplay> getLiftDisplays() {
        return liftDisplays;
    }
}
