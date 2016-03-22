package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class MBTFPQProjection extends Projection {
    private static final int NITER = 20;
    private static final double EPS = 1.0E-7D;
    private static final double ONETOL = 1.000001D;
    private static final double C = 1.7071067811865475D;
    private static final double RC = 0.585786437626905D;
    private static final double FYC = 1.874758284622695D;
    private static final double RYC = 0.533402096794177D;
    private static final double FXC = 0.3124597141037825D;
    private static final double RXC = 3.2004125807650623D;

    public MBTFPQProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double c = 1.7071067811865475D * Math.sin(lpphi);

        for(int i = 20; i > 0; --i) {
            double th1;
            out.y -= th1 = (Math.sin(0.5D * lpphi) + Math.sin(lpphi) - c) / (0.5D * Math.cos(0.5D * lpphi) + Math.cos(lpphi));
            if(Math.abs(th1) < 1.0E-7D) {
                break;
            }
        }

        out.x = 0.3124597141037825D * lplam * (1.0D + 2.0D * Math.cos(lpphi) / Math.cos(0.5D * lpphi));
        out.y = 1.874758284622695D * Math.sin(0.5D * lpphi);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double t = 0.0D;
        double lpphi = 0.533402096794177D * xyy;
        if(Math.abs(lpphi) > 1.0D) {
            if(Math.abs(lpphi) > 1.000001D) {
                throw new ProjectionException("I");
            }

            if(lpphi < 0.0D) {
                t = -1.0D;
                lpphi = -3.141592653589793D;
            } else {
                t = 1.0D;
                lpphi = 3.141592653589793D;
            }
        } else {
            t = lpphi;
            lpphi = 2.0D * Math.asin(lpphi);
        }

        out.x = 3.2004125807650623D * xyx / (1.0D + 2.0D * Math.cos(lpphi) / Math.cos(0.5D * lpphi));
        lpphi = 0.585786437626905D * (t + Math.sin(lpphi));
        if(Math.abs(lpphi) > 1.0D) {
            if(Math.abs(lpphi) > 1.000001D) {
                throw new ProjectionException("I");
            }

            lpphi = lpphi < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        } else {
            lpphi = Math.asin(lpphi);
        }

        out.y = lpphi;
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "McBryde-Thomas Flat-Polar Quartic";
    }
}
