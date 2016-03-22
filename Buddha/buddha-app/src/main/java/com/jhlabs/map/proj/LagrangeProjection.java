package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class LagrangeProjection extends Projection {
    private double hrw;
    private double rw = 1.4D;
    private double a1;
    private double phi1;
    private static final double TOL = 1.0E-10D;

    public LagrangeProjection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        if(Math.abs(Math.abs(lpphi) - 1.5707963267948966D) < 1.0E-10D) {
            xy.x = 0.0D;
            xy.y = lpphi < 0.0D?-2.0D:2.0D;
        } else {
            lpphi = Math.sin(lpphi);
            double v = this.a1 * Math.pow((1.0D + lpphi) / (1.0D - lpphi), this.hrw);
            double c;
            if((c = 0.5D * (v + 1.0D / v) + Math.cos(lplam *= this.rw)) < 1.0E-10D) {
                throw new ProjectionException();
            }

            xy.x = 2.0D * Math.sin(lplam) / c;
            xy.y = (v - 1.0D / v) / c;
        }

        return xy;
    }

    public void setW(double w) {
        this.rw = w;
    }

    public double getW() {
        return this.rw;
    }

    public void initialize() {
        super.initialize();
        if(this.rw <= 0.0D) {
            throw new ProjectionException("-27");
        } else {
            this.hrw = 0.5D * (this.rw = 1.0D / this.rw);
            this.phi1 = this.projectionLatitude1;
            if(Math.abs(Math.abs(this.phi1 = Math.sin(this.phi1)) - 1.0D) < 1.0E-10D) {
                throw new ProjectionException("-22");
            } else {
                this.a1 = Math.pow((1.0D - this.phi1) / (1.0D + this.phi1), this.hrw);
            }
        }
    }

    public boolean isConformal() {
        return true;
    }

    public boolean hasInverse() {
        return false;
    }

    public String toString() {
        return "Lagrange";
    }
}
