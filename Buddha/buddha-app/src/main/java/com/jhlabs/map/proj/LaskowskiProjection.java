package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class LaskowskiProjection extends Projection {
    private static final double a10 = 0.975534D;
    private static final double a12 = -0.119161D;
    private static final double a32 = -0.0143059D;
    private static final double a14 = -0.0547009D;
    private static final double b01 = 1.00384D;
    private static final double b21 = 0.0802894D;
    private static final double b03 = 0.0998909D;
    private static final double b41 = 1.99025E-4D;
    private static final double b23 = -0.02855D;
    private static final double b05 = -0.0491032D;

    public LaskowskiProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double l2 = lplam * lplam;
        double p2 = lpphi * lpphi;
        out.x = lplam * (0.975534D + p2 * (-0.119161D + l2 * -0.0143059D + p2 * -0.0547009D));
        out.y = lpphi * (1.00384D + l2 * (0.0802894D + p2 * -0.02855D + l2 * 1.99025E-4D) + p2 * (0.0998909D + p2 * -0.0491032D));
        return out;
    }

    public String toString() {
        return "Laskowski";
    }
}
