package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class TCEAProjection extends Projection {
    private double rk0;

    public TCEAProjection() {
        this.initialize();
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = this.rk0 * Math.cos(lpphi) * Math.sin(lplam);
        out.y = this.scaleFactor * (Math.atan2(Math.tan(lpphi), Math.cos(lplam)) - this.projectionLatitude);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = xyy * this.rk0 + this.projectionLatitude;
        out.x *= this.scaleFactor;
        double t = Math.sqrt(1.0D - xyx * xyx);
        out.y = Math.asin(t * Math.sin(xyy));
        out.x = Math.atan2(xyx, t * Math.cos(xyy));
        return out;
    }

    public void initialize() {
        super.initialize();
        this.rk0 = 1.0D / this.scaleFactor;
    }

    public boolean isRectilinear() {
        return false;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Transverse Cylindrical Equal Area";
    }
}
