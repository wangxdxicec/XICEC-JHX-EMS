package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class PerspectiveProjection extends Projection {
    private double height;
    private double psinph0;
    private double pcosph0;
    private double p;
    private double rp;
    private double pn1;
    private double pfact;
    private double h;
    private double cg;
    private double sg;
    private double sw;
    private double cw;
    private int mode;
    private int tilt;
    private static final double EPS10 = 1.0E-10D;
    private static final int N_POLE = 0;
    private static final int S_POLE = 1;
    private static final int EQUIT = 2;
    private static final int OBLIQ = 3;

    public PerspectiveProjection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        double sinphi = Math.sin(lpphi);
        double cosphi = Math.cos(lpphi);
        double coslam = Math.cos(lplam);
        switch(this.mode) {
            case 0:
                xy.y = sinphi;
                break;
            case 1:
                xy.y = -sinphi;
                break;
            case 2:
                xy.y = cosphi * coslam;
                break;
            case 3:
                xy.y = this.psinph0 * sinphi + this.pcosph0 * cosphi * coslam;
        }

        xy.y = this.pn1 / (this.p - xy.y);
        xy.x = xy.y * cosphi * Math.sin(lplam);
        switch(this.mode) {
            case 0:
                coslam = -coslam;
            case 1:
                xy.y *= cosphi * coslam;
                break;
            case 2:
                xy.y *= sinphi;
                break;
            case 3:
                xy.y *= this.pcosph0 * sinphi - this.psinph0 * cosphi * coslam;
        }

        if(this.tilt != 0) {
            double yt = xy.y * this.cg + xy.x * this.sg;
            double ba = 1.0D / (yt * this.sw * this.h + this.cw);
            xy.x = (xy.x * this.cg - xy.y * this.sg) * this.cw * ba;
            xy.y = yt * ba;
        }

        return xy;
    }

    public boolean hasInverse() {
        return false;
    }

    public void initialize() {
        super.initialize();
        this.mode = 2;
        this.height = this.a;
        this.tilt = 0;
        this.pn1 = this.height / this.a;
        this.p = 1.0D + this.pn1;
        this.rp = 1.0D / this.p;
        this.h = 1.0D / this.pn1;
        this.pfact = (this.p + 1.0D) * this.h;
        this.es = 0.0D;
    }

    public String toString() {
        return "Perspective";
    }
}
