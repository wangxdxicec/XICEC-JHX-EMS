package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.CylindricalProjection;

public class MillerProjection extends CylindricalProjection {
    public MillerProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = lplam;
        out.y = Math.log(Math.tan(0.7853981633974483D + lpphi * 0.4D)) * 1.25D;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.x = xyx;
        out.y = 2.5D * (Math.atan(Math.exp(0.8D * xyy)) - 0.7853981633974483D);
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Miller Cylindrical";
    }
}
