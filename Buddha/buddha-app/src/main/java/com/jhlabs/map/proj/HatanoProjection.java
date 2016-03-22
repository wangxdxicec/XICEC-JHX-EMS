package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class HatanoProjection extends Projection {
    private static final int NITER = 20;
    private static final double EPS = 1.0E-7D;
    private static final double ONETOL = 1.000001D;
    private static final double CN = 2.67595D;
    private static final double CS = 2.43763D;
    private static final double RCN = 0.3736990601468637D;
    private static final double RCS = 0.4102345310814193D;
    private static final double FYCN = 1.75859D;
    private static final double FYCS = 1.93052D;
    private static final double RYCN = 0.5686373742600607D;
    private static final double RYCS = 0.5179951515653813D;
    private static final double FXC = 0.85D;
    private static final double RXC = 1.1764705882352942D;

    public HatanoProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double c = Math.sin(lpphi) * (lpphi < 0.0D?2.43763D:2.67595D);

        for(int i = 20; i > 0; --i) {
            double th1;
            lpphi -= th1 = (lpphi + Math.sin(lpphi) - c) / (1.0D + Math.cos(lpphi));
            if(Math.abs(th1) < 1.0E-7D) {
                break;
            }
        }

        out.x = 0.85D * lplam * Math.cos(lpphi *= 0.5D);
        out.y = Math.sin(lpphi) * (lpphi < 0.0D?1.93052D:1.75859D);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double th = xyy * (xyy < 0.0D?0.5179951515653813D:0.5686373742600607D);
        if(Math.abs(th) > 1.0D) {
            if(Math.abs(th) > 1.000001D) {
                throw new ProjectionException("I");
            }

            th = th > 0.0D?1.5707963267948966D:-1.5707963267948966D;
        } else {
            th = Math.asin(th);
        }

        out.x = 1.1764705882352942D * xyx / Math.cos(th);
        th += th;
        out.y = (th + Math.sin(th)) * (xyy < 0.0D?0.4102345310814193D:0.3736990601468637D);
        if(Math.abs(out.y) > 1.0D) {
            if(Math.abs(out.y) > 1.000001D) {
                throw new ProjectionException("I");
            }

            out.y = out.y > 0.0D?1.5707963267948966D:-1.5707963267948966D;
        } else {
            out.y = Math.asin(out.y);
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Hatano Asymmetrical Equal Area";
    }
}
