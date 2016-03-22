package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class Wagner3Projection extends PseudoCylindricalProjection {
    private static final double TWOTHIRD = 0.6666666666666666D;
    private double C_x;

    public Wagner3Projection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        xy.x = this.C_x * lplam * Math.cos(0.6666666666666666D * lpphi);
        xy.y = lpphi;
        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        lp.y = y;
        lp.x = x / (this.C_x * Math.cos(0.6666666666666666D * lp.y));
        return lp;
    }

    public void initialize() {
        super.initialize();
        this.C_x = Math.cos(this.trueScaleLatitude) / Math.cos(2.0D * this.trueScaleLatitude / 3.0D);
        this.es = 0.0D;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Wagner III";
    }
}
