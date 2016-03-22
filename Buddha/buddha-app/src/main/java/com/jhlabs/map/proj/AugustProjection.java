package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class AugustProjection extends Projection {
    private static final double M = 1.333333333333333D;

    public AugustProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double t = Math.tan(0.5D * lpphi);
        double c1 = Math.sqrt(1.0D - t * t);
        double c = 1.0D + c1 * Math.cos(lplam *= 0.5D);
        double x1 = Math.sin(lplam) * c1 / c;
        double y1 = t / c;
        double y12;
        double x12;
        out.x = 1.333333333333333D * x1 * (3.0D + (x12 = x1 * x1) - 3.0D * (y12 = y1 * y1));
        out.y = 1.333333333333333D * y1 * (3.0D + 3.0D * x12 - y12);
        return out;
    }

    public boolean isConformal() {
        return true;
    }

    public boolean hasInverse() {
        return false;
    }

    public String toString() {
        return "August Epicycloidal";
    }
}