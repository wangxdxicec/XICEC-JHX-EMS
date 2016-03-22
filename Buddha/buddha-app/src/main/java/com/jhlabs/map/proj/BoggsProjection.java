package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class BoggsProjection extends PseudoCylindricalProjection {
    private static final int NITER = 20;
    private static final double EPS = 1.0E-7D;
    private static final double ONETOL = 1.000001D;
    private static final double FXC = 2.00276D;
    private static final double FXC2 = 1.11072D;
    private static final double FYC = 0.49931D;
    private static final double FYC2 = 1.4142135623730951D;

    public BoggsProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double theta = lpphi;
        if(Math.abs(Math.abs(lpphi) - 1.5707963267948966D) < 1.0E-7D) {
            out.x = 0.0D;
        } else {
            double c = Math.sin(lpphi) * 3.141592653589793D;

            for(int i = 20; i > 0; --i) {
                double th1;
                theta -= th1 = (theta + Math.sin(theta) - c) / (1.0D + Math.cos(theta));
                if(Math.abs(th1) < 1.0E-7D) {
                    break;
                }
            }

            theta *= 0.5D;
            out.x = 2.00276D * lplam / (1.0D / Math.cos(lpphi) + 1.11072D / Math.cos(theta));
        }

        out.y = 0.49931D * (lpphi + 1.4142135623730951D * Math.sin(theta));
        return out;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return false;
    }

    public String toString() {
        return "Boggs Eumorphic";
    }
}
