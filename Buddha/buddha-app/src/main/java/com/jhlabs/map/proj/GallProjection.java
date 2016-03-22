package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class GallProjection extends Projection {
    private static final double YF = 1.7071067811865475D;
    private static final double XF = 0.7071067811865476D;
    private static final double RYF = 0.585786437626905D;
    private static final double RXF = 1.4142135623730951D;

    public GallProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = 0.7071067811865476D * lplam;
        out.y = 1.7071067811865475D * Math.tan(0.5D * lpphi);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.x = 1.4142135623730951D * xyx;
        out.y = 2.0D * Math.atan(xyy * 0.585786437626905D);
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Gall (Gall Stereographic)";
    }
}
