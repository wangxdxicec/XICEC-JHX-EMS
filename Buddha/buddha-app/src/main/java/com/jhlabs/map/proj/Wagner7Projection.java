package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class Wagner7Projection extends Projection {
    public Wagner7Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double theta = Math.asin(out.y = 0.9063077870366499D * Math.sin(lpphi));
        double ct;
        out.x = 2.66723D * (ct = Math.cos(theta)) * Math.sin(lplam /= 3.0D);
        double D;
        out.y *= 1.24104D * (D = 1.0D / Math.sqrt(0.5D * (1.0D + ct * Math.cos(lplam))));
        out.x *= D;
        return out;
    }

    public boolean isEqualArea() {
        return true;
    }

    public String toString() {
        return "Wagner VII";
    }
}
