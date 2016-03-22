package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class SinusoidalProjection extends PseudoCylindricalProjection {
    public SinusoidalProjection() {
    }

    public Double project(double lam, double phi, Double xy) {
        xy.x = lam * Math.cos(phi);
        xy.y = phi;
        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        lp.x = x / Math.cos(y);
        lp.y = y;
        return lp;
    }

    public double getWidth(double y) {
        return MapMath.normalizeLongitude(3.141592653589793D) * Math.cos(y);
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Sinusoidal";
    }
}
