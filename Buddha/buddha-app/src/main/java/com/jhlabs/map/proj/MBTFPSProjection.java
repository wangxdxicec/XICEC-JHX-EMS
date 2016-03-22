package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class MBTFPSProjection extends Projection {
    private static final int MAX_ITER = 10;
    private static final double LOOP_TOL = 1.0E-7D;
    private static final double C1 = 0.45503D;
    private static final double C2 = 1.36509D;
    private static final double C3 = 1.41546D;
    private static final double C_x = 0.22248D;
    private static final double C_y = 1.44492D;
    private static final double C1_2 = 0.3333333333333333D;

    public MBTFPSProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double k = 1.41546D * Math.sin(lpphi);

        double t;
        for(int i = 10; i > 0; --i) {
            t = lpphi / 1.36509D;
            double V;
            out.y -= V = (0.45503D * Math.sin(t) + Math.sin(lpphi) - k) / (0.3333333333333333D * Math.cos(t) + Math.cos(lpphi));
            if(Math.abs(V) < 1.0E-7D) {
                break;
            }
        }

        t = lpphi / 1.36509D;
        out.x = 0.22248D * lplam * (1.0D + 3.0D * Math.cos(lpphi) / Math.cos(t));
        out.y = 1.44492D * Math.sin(t);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double t;
        out.y = 1.36509D * (t = MapMath.asin(xyy / 1.44492D));
        out.x = xyx / (0.22248D * (1.0D + 3.0D * Math.cos(out.y) / Math.cos(t)));
        out.y = MapMath.asin((0.45503D * Math.sin(t) + Math.sin(out.y)) / 1.41546D);
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "McBryde-Thomas Flat-Pole Sine (No. 2)";
    }
}
