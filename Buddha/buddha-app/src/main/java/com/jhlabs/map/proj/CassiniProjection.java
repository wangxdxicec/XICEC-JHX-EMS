package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class CassiniProjection extends Projection {
    private double m0;
    private double n;
    private double t;
    private double a1;
    private double c;
    private double r;
    private double dd;
    private double d2;
    private double a2;
    private double tn;
    private double[] en;
    private static final double EPS10 = 1.0E-10D;
    private static final double C1 = 0.16666666666666666D;
    private static final double C2 = 0.008333333333333333D;
    private static final double C3 = 0.041666666666666664D;
    private static final double C4 = 0.3333333333333333D;
    private static final double C5 = 0.06666666666666667D;

    public CassiniProjection() {
        this.projectionLatitude = Math.toRadians(0.0D);
        this.projectionLongitude = Math.toRadians(0.0D);
        this.minLongitude = Math.toRadians(-90.0D);
        this.maxLongitude = Math.toRadians(90.0D);
        this.initialize();
    }

    public Double project(double lplam, double lpphi, Double xy) {
        if(this.spherical) {
            xy.x = Math.asin(Math.cos(lpphi) * Math.sin(lplam));
            xy.y = Math.atan2(Math.tan(lpphi), Math.cos(lplam)) - this.projectionLatitude;
        } else {
            xy.y = MapMath.mlfn(lpphi, this.n = Math.sin(lpphi), this.c = Math.cos(lpphi), this.en);
            this.n = 1.0D / Math.sqrt(1.0D - this.es * this.n * this.n);
            this.tn = Math.tan(lpphi);
            this.t = this.tn * this.tn;
            this.a1 = lplam * this.c;
            this.c *= this.es * this.c / (1.0D - this.es);
            this.a2 = this.a1 * this.a1;
            xy.x = this.n * this.a1 * (1.0D - this.a2 * this.t * (0.16666666666666666D - (8.0D - this.t + 8.0D * this.c) * this.a2 * 0.008333333333333333D));
            xy.y -= this.m0 - this.n * this.tn * this.a2 * (0.5D + (5.0D - this.t + 6.0D * this.c) * this.a2 * 0.041666666666666664D);
        }

        return xy;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        if(this.spherical) {
            out.y = Math.asin(Math.sin(this.dd = xyy + this.projectionLatitude) * Math.cos(xyx));
            out.x = Math.atan2(Math.tan(xyx), Math.cos(this.dd));
        } else {
            double ph1 = MapMath.inv_mlfn(this.m0 + xyy, this.es, this.en);
            this.tn = Math.tan(ph1);
            this.t = this.tn * this.tn;
            this.n = Math.sin(ph1);
            this.r = 1.0D / (1.0D - this.es * this.n * this.n);
            this.n = Math.sqrt(this.r);
            this.r *= (1.0D - this.es) * this.n;
            this.dd = xyx / this.n;
            this.d2 = this.dd * this.dd;
            out.y = ph1 - this.n * this.tn / this.r * this.d2 * (0.5D - (1.0D + 3.0D * this.t) * this.d2 * 0.041666666666666664D);
            out.x = this.dd * (1.0D + this.t * this.d2 * (-0.3333333333333333D + (1.0D + 3.0D * this.t) * this.d2 * 0.06666666666666667D)) / Math.cos(ph1);
        }

        return out;
    }

    public void initialize() {
        super.initialize();
        if(!this.spherical) {
            if((this.en = MapMath.enfn(this.es)) == null) {
                throw new IllegalArgumentException();
            }

            this.m0 = MapMath.mlfn(this.projectionLatitude, Math.sin(this.projectionLatitude), Math.cos(this.projectionLatitude), this.en);
        }

    }

    public boolean hasInverse() {
        return true;
    }

    public int getEPSGCode() {
        return 9806;
    }

    public String toString() {
        return "Cassini";
    }
}
