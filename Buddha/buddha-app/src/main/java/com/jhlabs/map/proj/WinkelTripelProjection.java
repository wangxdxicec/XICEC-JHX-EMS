package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.AitoffProjection;

public class WinkelTripelProjection extends AitoffProjection {
    public WinkelTripelProjection() {
        super(1, 0.6366197723675814D);
    }

    public String toString() {
        return "Winkel Tripel";
    }
}
