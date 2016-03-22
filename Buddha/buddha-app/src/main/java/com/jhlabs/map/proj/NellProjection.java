package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class NellProjection extends Projection {
    private static final int MAX_ITER = 10;
    private static final double LOOP_TOL = 1.0E-7D;

    public NellProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double k = 2.0D * Math.sin(lpphi);
        double V = lpphi * lpphi;
        out.y *= 1.00371D + V * (-0.0935382D + V * -0.011412D);

        for(int i = 10; i > 0; --i) {
            out.y -= V = (lpphi + Math.sin(lpphi) - k) / (1.0D + Math.cos(lpphi));
            if(Math.abs(V) < 1.0E-7D) {
                break;
            }
        }

        out.x = 0.5D * lplam * (1.0D + Math.cos(lpphi));
        out.y = lpphi;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.x = 2.0D * xyx / (1.0D + Math.cos(xyy));
        out.y = MapMath.asin(0.5D * (xyy + Math.sin(xyy)));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Nell";
    }
}
