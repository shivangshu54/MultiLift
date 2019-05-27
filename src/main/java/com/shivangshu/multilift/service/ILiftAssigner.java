package com.shivangshu.multilift.service;

import com.shivangshu.multilift.commons.Lift;
import com.shivangshu.multilift.controller.request.ExternalRequest;

public interface ILiftAssigner {

    public Lift assignLift(ExternalRequest r);
}
