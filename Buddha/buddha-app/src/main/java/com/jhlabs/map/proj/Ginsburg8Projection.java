package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class Ginsburg8Projection extends Projection {
    private static final double Cl = 9.52426E-4D;
    private static final double Cp = 0.162388D;
    private static final double C12 = 0.08333333333333333D;

    public Ginsburg8Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double t = lpphi * lpphi;
        out.y = lpphi * (1.0D + t * 0.08333333333333333D);
        out.x = lplam * (1.0D - 0.162388D * t);
        t = lplam * lplam;
        out.x *= 0.87D - 9.52426E-4D * t * t;
        return out;
    }

    public String toString() {
        return "Ginsburg VIII (TsNIIGAiK)";
    }
}
