package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class LarriveeProjection extends Projection {
    private static final double SIXTH = 0.16666666666666666D;

    public LarriveeProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = 0.5D * lplam * (1.0D + Math.sqrt(Math.cos(lpphi)));
        out.y = lpphi / (Math.cos(0.5D * lpphi) * Math.cos(0.16666666666666666D * lplam));
        return out;
    }

    public String toString() {
        return "Larrivee";
    }
}
