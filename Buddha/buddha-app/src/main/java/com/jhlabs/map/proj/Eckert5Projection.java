package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class Eckert5Projection extends Projection {
    private static final double XF = 0.4410127717245515D;
    private static final double RXF = 2.267508027238226D;
    private static final double YF = 0.882025543449103D;
    private static final double RYF = 1.133754013619113D;

    public Eckert5Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.x = 0.4410127717245515D * (1.0D + Math.cos(lpphi)) * lplam;
        out.y = 0.882025543449103D * lpphi;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.x = 2.267508027238226D * xyx / (1.0D + Math.cos(out.y = 1.133754013619113D * xyy));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Eckert V";
    }
}
