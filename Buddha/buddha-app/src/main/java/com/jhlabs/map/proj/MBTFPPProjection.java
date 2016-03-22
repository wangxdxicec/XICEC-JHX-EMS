package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class MBTFPPProjection extends Projection {
    private static final double CS = 0.9525793444156804D;
    private static final double FXC = 0.9258200997725514D;
    private static final double FYC = 3.401680257083045D;
    private static final double C23 = 0.6666666666666666D;
    private static final double C13 = 0.3333333333333333D;
    private static final double ONEEPS = 1.0000001D;

    public MBTFPPProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.y = Math.asin(0.9525793444156804D * Math.sin(lpphi));
        out.x = 0.9258200997725514D * lplam * (2.0D * Math.cos(0.6666666666666666D * lpphi) - 1.0D);
        out.y = 3.401680257083045D * Math.sin(0.3333333333333333D * lpphi);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = xyy / 3.401680257083045D;
        if(Math.abs(out.y) >= 1.0D) {
            if(Math.abs(out.y) > 1.0000001D) {
                throw new ProjectionException("I");
            }

            out.y = out.y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        } else {
            out.y = Math.asin(out.y);
        }

        out.x = xyx / (0.9258200997725514D * (2.0D * Math.cos(0.6666666666666666D * (out.y *= 3.0D)) - 1.0D));
        if(Math.abs(out.y = Math.sin(out.y) / 0.9525793444156804D) >= 1.0D) {
            if(Math.abs(out.y) > 1.0000001D) {
                throw new ProjectionException("I");
            }

            out.y = out.y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        } else {
            out.y = Math.asin(out.y);
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "McBride-Thomas Flat-Polar Parabolic";
    }
}
