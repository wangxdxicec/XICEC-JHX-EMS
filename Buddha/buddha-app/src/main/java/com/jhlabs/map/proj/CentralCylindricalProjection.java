package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.CylindricalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class CentralCylindricalProjection extends CylindricalProjection {
    private double ap;
    private static final double EPS10 = 1.0E-10D;

    public CentralCylindricalProjection() {
        this.minLatitude = Math.toRadians(-80.0D);
        this.maxLatitude = Math.toRadians(80.0D);
    }

    public Double project(double lplam, double lpphi, Double out) {
        if(Math.abs(Math.abs(lpphi) - 1.5707963267948966D) <= 1.0E-10D) {
            throw new ProjectionException("F");
        } else {
            out.x = lplam;
            out.y = Math.tan(lpphi);
            return out;
        }
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = Math.atan(xyy);
        out.x = xyx;
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Central Cylindrical";
    }
}
