package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.ProjectionException;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class HammerProjection extends PseudoCylindricalProjection {
    private double w = 0.5D;
    private double m = 1.0D;
    private double rm;

    public HammerProjection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        double cosphi;
        double d = Math.sqrt(2.0D / (1.0D + (cosphi = Math.cos(lpphi)) * Math.cos(lplam *= this.w)));
        xy.x = this.m * d * cosphi * Math.sin(lplam);
        xy.y = this.rm * d * Math.sin(lpphi);
        return xy;
    }

    public void initialize() {
        super.initialize();
        if((this.w = Math.abs(this.w)) <= 0.0D) {
            throw new ProjectionException("-27");
        } else {
            this.w = 0.5D;
            if((this.m = Math.abs(this.m)) <= 0.0D) {
                throw new ProjectionException("-27");
            } else {
                this.m = 1.0D;
                this.rm = 1.0D / this.m;
                this.m /= this.w;
                this.es = 0.0D;
            }
        }
    }

    public boolean isEqualArea() {
        return true;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getW() {
        return this.w;
    }

    public void setM(double m) {
        this.m = m;
    }

    public double getM() {
        return this.m;
    }

    public String toString() {
        return "Hammer & Eckert-Greifendorff";
    }
}
