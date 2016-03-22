package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.PutninsP4Projection;

public class WerenskioldProjection extends PutninsP4Projection {
    public WerenskioldProjection() {
        this.C_x = 1.0D;
        this.C_y = 4.442882938D;
    }

    public String toString() {
        return "Werenskiold I";
    }
}
