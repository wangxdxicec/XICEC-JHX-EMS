package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class FaheyProjection extends Projection {
    private static final double TOL = 1.0E-6D;

    public FaheyProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.y = 1.819152D * (out.x = Math.tan(0.5D * lpphi));
        out.x = 0.819152D * lplam * this.asqrt(1.0D - out.x * out.x);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = 2.0D * Math.atan(out.y /= 1.819152D);
        out.x = Math.abs(out.y = 1.0D - xyy * xyy) < 1.0E-6D?0.0D:xyx / (0.819152D * Math.sqrt(xyy));
        return out;
    }

    private double asqrt(double v) {
        return v <= 0.0D?0.0D:Math.sqrt(v);
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Fahey";
    }
}
