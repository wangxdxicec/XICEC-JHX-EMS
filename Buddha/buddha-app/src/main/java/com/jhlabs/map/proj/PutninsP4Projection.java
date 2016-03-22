package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class PutninsP4Projection extends Projection {
    protected double C_x = 0.874038744D;
    protected double C_y = 3.883251825D;

    public PutninsP4Projection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        lpphi = MapMath.asin(0.883883476D * Math.sin(lpphi));
        xy.x = this.C_x * lplam * Math.cos(lpphi);
        xy.x /= Math.cos(lpphi *= 0.333333333333333D);
        xy.y = this.C_y * Math.sin(lpphi);
        return xy;
    }

    public Double projectInverse(double xyx, double xyy, Double lp) {
        lp.y = MapMath.asin(xyy / this.C_y);
        lp.x = xyx * Math.cos(lp.y) / this.C_x;
        lp.y *= 3.0D;
        lp.x /= Math.cos(lp.y);
        lp.y = MapMath.asin(1.13137085D * Math.sin(lp.y));
        return lp;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Putnins P4";
    }
}
