package com.shivangshu.multilift.controller.request;

import com.shivangshu.multilift.commons.RequestedDirection;
import lombok.Data;

@Data
public class ExternalRequest {

    private int fromFloor;
    private RequestedDirection requestedDirection;

}
