package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class PutninsP5Projection extends Projection {
    protected double A = 2.0D;
    protected double B = 1.0D;
    private static final double C = 1.01346D;
    private static final double D = 1.2158542D;

    public PutninsP5Projection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        xy.x = 1.01346D * lplam * (this.A - this.B * Math.sqrt(1.0D + 1.2158542D * lpphi * lpphi));
        xy.y = 1.01346D * lpphi;
        return xy;
    }

    public Double projectInverse(double xyx, double xyy, Double lp) {
        lp.y = xyy / 1.01346D;
        lp.x = xyx / (1.01346D * (this.A - this.B * Math.sqrt(1.0D + 1.2158542D * lp.y * lp.y)));
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Putnins P5";
    }
}
