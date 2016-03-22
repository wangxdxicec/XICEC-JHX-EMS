package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class Eckert2Projection extends Projection {
    private static final double FXC = 0.46065886596178063D;
    private static final double FYC = 1.4472025091165353D;
    private static final double C13 = 0.3333333333333333D;
    private static final double ONEEPS = 1.0000001D;

    public Eckert2Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = 0.46065886596178063D * lplam * (out.y = Math.sqrt(4.0D - 3.0D * Math.sin(Math.abs(lpphi))));
        out.y = 1.4472025091165353D * (2.0D - out.y);
        if(lpphi < 0.0D) {
            out.y = -out.y;
        }

        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.x = xyx / (0.46065886596178063D * (out.y = 2.0D - Math.abs(xyy) / 1.4472025091165353D));
        out.y = (4.0D - out.y * out.y) * 0.3333333333333333D;
        if(Math.abs(out.y) >= 1.0D) {
            if(Math.abs(out.y) > 1.0000001D) {
                throw new ProjectionException("I");
            }

            out.y = out.y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        } else {
            out.y = Math.asin(out.y);
        }

        if(xyy < 0.0D) {
            out.y = -out.y;
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Eckert II";
    }
}
