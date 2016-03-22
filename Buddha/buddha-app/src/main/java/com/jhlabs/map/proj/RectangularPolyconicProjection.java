package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class RectangularPolyconicProjection extends Projection {
    private double phi0;
    private double phi1;
    private double fxa;
    private double fxb;
    private boolean mode;
    private static final double EPS = 1.0E-9D;

    public RectangularPolyconicProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double fa;
        if(this.mode) {
            fa = Math.tan(lplam * this.fxb) * this.fxa;
        } else {
            fa = 0.5D * lplam;
        }

        if(Math.abs(lpphi) < 1.0E-9D) {
            out.x = fa + fa;
            out.y = -this.phi0;
        } else {
            out.y = 1.0D / Math.tan(lpphi);
            out.x = Math.sin(fa = 2.0D * Math.atan(fa * Math.sin(lpphi))) * out.y;
            out.y = lpphi - this.phi0 + (1.0D - Math.cos(fa)) * out.y;
        }

        return out;
    }

    public void initialize() {
        super.initialize();
    }

    public String toString() {
        return "Rectangular Polyconic";
    }
}
