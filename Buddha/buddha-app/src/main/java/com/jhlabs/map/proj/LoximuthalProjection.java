package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class LoximuthalProjection extends PseudoCylindricalProjection {
    private static final double FC = 0.9213177319235613D;
    private static final double RP = 0.3183098861837907D;
    private static final double EPS = 1.0E-8D;
    private double phi1 = Math.toRadians(40.0D);
    private double cosphi1;
    private double tanphi1;

    public LoximuthalProjection() {
        this.cosphi1 = Math.cos(this.phi1);
        this.tanphi1 = Math.tan(0.7853981633974483D + 0.5D * this.phi1);
    }

    public Double project(double lplam, double lpphi, Double out) {
        double y = lpphi - this.phi1;
        double x;
        if(y < 1.0E-8D) {
            x = lplam * this.cosphi1;
        } else {
            x = 0.7853981633974483D + 0.5D * lpphi;
            if(Math.abs(x) >= 1.0E-8D && Math.abs(Math.abs(x) - 1.5707963267948966D) >= 1.0E-8D) {
                x = lplam * y / Math.log(Math.tan(x) / this.tanphi1);
            } else {
                x = 0.0D;
            }
        }

        out.x = x;
        out.y = y;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double latitude = xyy + this.phi1;
        double longitude;
        if(Math.abs(xyy) < 1.0E-8D) {
            longitude = xyx / this.cosphi1;
        } else if(Math.abs(longitude = 0.7853981633974483D + 0.5D * xyy) >= 1.0E-8D && Math.abs(Math.abs(xyx) - 1.5707963267948966D) >= 1.0E-8D) {
            longitude = xyx * Math.log(Math.tan(longitude) / this.tanphi1) / xyy;
        } else {
            longitude = 0.0D;
        }

        out.x = longitude;
        out.y = latitude;
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Loximuthal";
    }
}
