package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class AitoffProjection extends PseudoCylindricalProjection {
    protected static final int AITOFF = 0;
    protected static final int WINKEL = 1;
    private boolean winkel = false;
    private double cosphi1 = 0.0D;

    public AitoffProjection() {
    }

    public AitoffProjection(int type, double projectionLatitude) {
        this.projectionLatitude = projectionLatitude;
        this.winkel = type == 1;
    }

    public Double project(double lplam, double lpphi, Double out) {
        double c = 0.5D * lplam;
        double d = Math.acos(Math.cos(lpphi) * Math.cos(c));
        if(d != 0.0D) {
            out.x = 2.0D * d * Math.cos(lpphi) * Math.sin(c) * (out.y = 1.0D / Math.sin(d));
            out.y *= d * Math.sin(lpphi);
        } else {
            out.x = out.y = 0.0D;
        }

        if(this.winkel) {
            out.x = (out.x + lplam * this.cosphi1) * 0.5D;
            out.y = (out.y + lpphi) * 0.5D;
        }

        return out;
    }

    public void initialize() {
        super.initialize();
        if(this.winkel) {
            this.cosphi1 = 0.6366197723675814D;
        }

    }

    public boolean hasInverse() {
        return false;
    }

    public String toString() {
        return this.winkel?"Winkel Tripel":"Aitoff";
    }
}
