package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class Eckert1Projection extends Projection {
    private static final double FC = 0.9213177319235613D;
    private static final double RP = 0.3183098861837907D;

    public Eckert1Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = 0.9213177319235613D * lplam * (1.0D - 0.3183098861837907D * Math.abs(lpphi));
        out.y = 0.9213177319235613D * lpphi;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = xyy / 0.9213177319235613D;
        out.x = xyx / (0.9213177319235613D * (1.0D - 0.3183098861837907D * Math.abs(out.y)));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Eckert I";
    }
}
