package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class Wagner2Projection extends Projection {
    private static final double C_x = 0.92483D;
    private static final double C_y = 1.38725D;
    private static final double C_p1 = 0.88022D;
    private static final double C_p2 = 0.8855D;

    public Wagner2Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.y = MapMath.asin(0.88022D * Math.sin(0.8855D * lpphi));
        out.x = 0.92483D * lplam * Math.cos(lpphi);
        out.y = 1.38725D * lpphi;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = xyy / 1.38725D;
        out.x = xyx / (0.92483D * Math.cos(out.y));
        out.y = MapMath.asin(Math.sin(out.y) / 0.88022D) / 0.8855D;
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Wagner II";
    }
}
