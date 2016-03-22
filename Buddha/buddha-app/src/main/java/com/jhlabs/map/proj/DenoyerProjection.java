package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.Projection;
import java.awt.geom.Point2D.Double;

public class DenoyerProjection extends Projection {
    public static final double C0 = 0.95D;
    public static final double C1 = -0.08333333333333333D;
    public static final double C3 = 0.0016666666666666666D;
    public static final double D1 = 0.9D;
    public static final double D5 = 0.03D;

    public DenoyerProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.y = lpphi;
        out.x = lplam;
        double aphi = Math.abs(lplam);
        out.x *= Math.cos((0.95D + aphi * (-0.08333333333333333D + aphi * aphi * 0.0016666666666666666D)) * lpphi * (0.9D + 0.03D * lpphi * lpphi * lpphi * lpphi));
        return out;
    }

    public boolean parallelsAreParallel() {
        return true;
    }

    public boolean hasInverse() {
        return false;
    }

    public String toString() {
        return "Denoyer Semi-elliptical";
    }
}
