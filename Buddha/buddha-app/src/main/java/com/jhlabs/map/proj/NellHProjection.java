package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class NellHProjection extends Projection {
    private static final int NITER = 9;
    private static final double EPS = 1.0E-7D;

    public NellHProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = 0.5D * lplam * (1.0D + Math.cos(lpphi));
        out.y = 2.0D * (lpphi - Math.tan(0.5D * lpphi));
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double p = 0.5D * xyy;

        int i;
        for(i = 9; i > 0; --i) {
            double c = Math.cos(0.5D * xyy);
            double V;
            out.y -= V = (xyy - Math.tan(xyy / 2.0D) - p) / (1.0D - 0.5D / (c * c));
            if(Math.abs(V) < 1.0E-7D) {
                break;
            }
        }

        if(i == 0) {
            out.y = p < 0.0D?-1.5707963267948966D:1.5707963267948966D;
            out.x = 2.0D * xyx;
        } else {
            out.x = 2.0D * xyx / (1.0D + Math.cos(xyy));
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Nell-Hammer";
    }
}
