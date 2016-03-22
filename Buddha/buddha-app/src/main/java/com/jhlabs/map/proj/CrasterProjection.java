package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class CrasterProjection extends Projection {
    private static final double XM = 0.9772050238058398D;
    private static final double RXM = 1.0233267079464885D;
    private static final double YM = 3.0699801238394655D;
    private static final double RYM = 0.32573500793527993D;
    private static final double THIRD = 0.3333333333333333D;

    public CrasterProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        lpphi *= 0.3333333333333333D;
        out.x = 0.9772050238058398D * lplam * (2.0D * Math.cos(lpphi + lpphi) - 1.0D);
        out.y = 3.0699801238394655D * Math.sin(lpphi);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = 3.0D * Math.asin(xyy * 0.32573500793527993D);
        out.x = xyx * 1.0233267079464885D / (2.0D * Math.cos((out.y + out.y) * 0.3333333333333333D) - 1.0D);
        return out;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Craster Parabolic (Putnins P4)";
    }
}
