package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.STSProjection;

public class MBTFPS1Projection extends STSProjection {
    public MBTFPS1Projection() {
        super(1.48875D, 1.36509D, false);
    }

    public String toString() {
        return "McBryde-Thomas Flat-Polar Sine (No. 1)";
    }
}
