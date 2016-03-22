package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class PutninsP2Projection extends Projection {
    private static final double C_x = 1.8949D;
    private static final double C_y = 1.71848D;
    private static final double C_p = 0.6141848493043784D;
    private static final double EPS = 1.0E-10D;
    private static final int NITER = 10;
    private static final double PI_DIV_3 = 1.0471975511965976D;

    public PutninsP2Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double p = 0.6141848493043784D * Math.sin(lpphi);
        double s = lpphi * lpphi;
        out.y *= 0.615709D + s * (0.00909953D + s * 0.0046292D);

        int i;
        for(i = 10; i > 0; --i) {
            double c = Math.cos(lpphi);
            s = Math.sin(lpphi);
            double V;
            out.y -= V = (lpphi + s * (c - 1.0D) - p) / (1.0D + c * (c - 1.0D) - s * s);
            if(Math.abs(V) < 1.0E-10D) {
                break;
            }
        }

        if(i == 0) {
            out.y = lpphi < 0.0D?-1.0471975511965976D:1.0471975511965976D;
        }

        out.x = 1.8949D * lplam * (Math.cos(lpphi) - 0.5D);
        out.y = 1.71848D * Math.sin(lpphi);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = MapMath.asin(xyy / 1.71848D);
        double c;
        out.x = xyx / (1.8949D * ((c = Math.cos(out.y)) - 0.5D));
        out.y = MapMath.asin((out.y + Math.sin(out.y) * (c - 1.0D)) / 0.6141848493043784D);
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Putnins P2";
    }
}
