package com.zhenhappy.ems.dto;

import com.zhenhappy.ems.entity.TExpoXicec;

/**
 * Created by wangxd on 2017-01-07.
 */
public class ExpoXicecPrinciple {

    public static final String PRINCIPLE_SESSION_ATTRIBUTE = "PRINCIPLE_SESSION_ATTRIBUTE";

    public ExpoXicecPrinciple(TExpoXicec tExpoXicec) {
        this.tExpoXicec = tExpoXicec;
    }

    private TExpoXicec tExpoXicec;

    public TExpoXicec gettExpoXicec() {
        return tExpoXicec;
    }

    public void settExpoXicec(TExpoXicec tExpoXicec) {
        this.tExpoXicec = tExpoXicec;
    }
}
