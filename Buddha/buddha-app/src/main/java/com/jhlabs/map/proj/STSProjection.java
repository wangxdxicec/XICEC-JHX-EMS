package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.ConicProjection;

class STSProjection extends ConicProjection {
    private double C_x;
    private double C_y;
    private double C_p;
    private boolean tan_mode;

    protected STSProjection(double p, double q, boolean mode) {
        this.es = 0.0D;
        this.C_x = q / p;
        this.C_y = p;
        this.C_p = 1.0D / q;
        this.tan_mode = mode;
        this.initialize();
    }

    public Double project(double lplam, double lpphi, Double xy) {
        xy.x = this.C_x * lplam * Math.cos(lpphi);
        xy.y = this.C_y;
        lpphi *= this.C_p;
        double c = Math.cos(lpphi);
        if(this.tan_mode) {
            xy.x *= c * c;
            xy.y *= Math.tan(lpphi);
        } else {
            xy.x /= c;
            xy.y *= Math.sin(lpphi);
        }

        return xy;
    }

    public Double projectInverse(double xyx, double xyy, Double lp) {
        xyy /= this.C_y;
        double c = Math.cos(lp.y = this.tan_mode?Math.atan(xyy):MapMath.asin(xyy));
        lp.y /= this.C_p;
        lp.x = xyx / (this.C_x * Math.cos(lp.y /= this.C_p));
        if(this.tan_mode) {
            lp.x /= c * c;
        } else {
            lp.x *= c;
        }

        return lp;
    }

    public boolean hasInverse() {
        return true;
    }
}
