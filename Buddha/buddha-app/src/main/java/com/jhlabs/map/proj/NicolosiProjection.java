package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class NicolosiProjection extends Projection {
    private static final double EPS = 1.0E-10D;

    public NicolosiProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        if(Math.abs(lplam) < 1.0E-10D) {
            out.x = 0.0D;
            out.y = lpphi;
        } else if(Math.abs(lpphi) < 1.0E-10D) {
            out.x = lplam;
            out.y = 0.0D;
        } else if(Math.abs(Math.abs(lplam) - 1.5707963267948966D) < 1.0E-10D) {
            out.x = lplam * Math.cos(lpphi);
            out.y = 1.5707963267948966D * Math.sin(lpphi);
        } else if(Math.abs(Math.abs(lpphi) - 1.5707963267948966D) < 1.0E-10D) {
            out.x = 0.0D;
            out.y = lpphi;
        } else {
            double tb = 1.5707963267948966D / lplam - lplam / 1.5707963267948966D;
            double c = lpphi / 1.5707963267948966D;
            double sp;
            double d = (1.0D - c * c) / ((sp = Math.sin(lpphi)) - c);
            double r2 = tb / d;
            r2 *= r2;
            double m = (tb * sp / d - 0.5D * tb) / (1.0D + r2);
            double n = (sp / r2 + 0.5D * d) / (1.0D + 1.0D / r2);
            double x = Math.cos(lpphi);
            x = Math.sqrt(m * m + x * x / (1.0D + r2));
            out.x = 1.5707963267948966D * (m + (lplam < 0.0D?-x:x));
            double y = Math.sqrt(n * n - (sp * sp / r2 + d * sp - 1.0D) / (1.0D + 1.0D / r2));
            out.y = 1.5707963267948966D * (n + (lpphi < 0.0D?y:-y));
        }

        return out;
    }

    public String toString() {
        return "Nicolosi Globular";
    }
}
