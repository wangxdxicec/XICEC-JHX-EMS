package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class CollignonProjection extends Projection {
    private static final double FXC = 1.1283791670955126D;
    private static final double FYC = 1.772453850905516D;
    private static final double ONEEPS = 1.0000001D;

    public CollignonProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        if((out.y = 1.0D - Math.sin(lpphi)) <= 0.0D) {
            out.y = 0.0D;
        } else {
            out.y = Math.sqrt(out.y);
        }

        out.x = 1.1283791670955126D * lplam * out.y;
        out.y = 1.772453850905516D * (1.0D - out.y);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double lpphi = xyy / 1.772453850905516D - 1.0D;
        if(Math.abs(out.y = 1.0D - lpphi * lpphi) < 1.0D) {
            out.y = Math.asin(lpphi);
        } else {
            if(Math.abs(lpphi) > 1.0000001D) {
                throw new ProjectionException("I");
            }

            out.y = lpphi < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        }

        if((out.x = 1.0D - Math.sin(lpphi)) <= 0.0D) {
            out.x = 0.0D;
        } else {
            out.x = xyx / (1.1283791670955126D * Math.sqrt(out.x));
        }

        out.y = lpphi;
        return out;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Collignon";
    }
}
