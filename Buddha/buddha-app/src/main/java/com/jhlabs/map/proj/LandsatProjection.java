package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class LandsatProjection extends Projection {
    private double a2;
    private double a4;
    private double b;
    private double c1;
    private double c3;
    private double q;
    private double t;
    private double u;
    private double w;
    private double p22;
    private double sa;
    private double ca;
    private double xj;
    private double rlm;
    private double rlm2;
    private static final double TOL = 1.0E-7D;
    private static final double PI_HALFPI = 4.71238898038469D;
    private static final double TWOPI_HALFPI = 7.853981633974483D;

    public LandsatProjection() {
    }

    public Double project(double lplam, double lpphi, Double xy) {
        double lamt = 0.0D;
        double lamdp = 0.0D;
        if(lpphi > 1.5707963267948966D) {
            lpphi = 1.5707963267948966D;
        } else if(lpphi < -1.5707963267948966D) {
            lpphi = -1.5707963267948966D;
        }

        double lampp = lpphi >= 0.0D?1.5707963267948966D:4.71238898038469D;
        double tanphi = Math.tan(lpphi);
        int nn = 0;

        int l;
        while(true) {
            double sav = lampp;
            double lamtp = lplam + this.p22 * lampp;
            double cl = Math.cos(lamtp);
            if(Math.abs(cl) < 1.0E-7D) {
                lamtp -= 1.0E-7D;
            }

            double fac = lampp - Math.sin(lampp) * (cl < 0.0D?-1.5707963267948966D:1.5707963267948966D);

            for(l = 50; l > 0; --l) {
                lamt = lplam + this.p22 * sav;
                double c;
                if(Math.abs(c = Math.cos(lamt)) < 1.0E-7D) {
                    lamt -= 1.0E-7D;
                }

                double xlam = (this.one_es * tanphi * this.sa + Math.sin(lamt) * this.ca) / c;
                lamdp = Math.atan(xlam) + fac;
                if(Math.abs(Math.abs(sav) - Math.abs(lamdp)) < 1.0E-7D) {
                    break;
                }

                sav = lamdp;
            }

            if(l == 0) {
                break;
            }

            ++nn;
            if(nn >= 3 || lamdp > this.rlm && lamdp < this.rlm2) {
                break;
            }

            if(lamdp <= this.rlm) {
                lampp = 7.853981633974483D;
            } else if(lamdp >= this.rlm2) {
                lampp = 1.5707963267948966D;
            }
        }

        if(l != 0) {
            double sp = Math.sin(lpphi);
            double phidp = MapMath.asin((this.one_es * this.ca * sp - this.sa * Math.cos(lpphi) * Math.sin(lamt)) / Math.sqrt(1.0D - this.es * sp * sp));
            double tanph = Math.log(Math.tan(0.7853981633974483D + 0.5D * phidp));
            double sd = Math.sin(lamdp);
            double sdsq = sd * sd;
            double s = this.p22 * this.sa * Math.cos(lamdp) * Math.sqrt((1.0D + this.t * sdsq) / ((1.0D + this.w * sdsq) * (1.0D + this.q * sdsq)));
            double d = Math.sqrt(this.xj * this.xj + s * s);
            xy.x = this.b * lamdp + this.a2 * Math.sin(2.0D * lamdp) + this.a4 * Math.sin(lamdp * 4.0D) - tanph * s / d;
            xy.y = this.c1 * sd + this.c3 * Math.sin(lamdp * 3.0D) + tanph * this.xj / d;
        } else {
            xy.x = xy.y = 1.0D / 0.0;
        }

        return xy;
    }

    private void seraz0(double lam, double mult) {
        lam *= 0.017453292519943295D;
        double sd = Math.sin(lam);
        double sdsq = sd * sd;
        double s = this.p22 * this.sa * Math.cos(lam) * Math.sqrt((1.0D + this.t * sdsq) / ((1.0D + this.w * sdsq) * (1.0D + this.q * sdsq)));
        double d__1 = 1.0D + this.q * sdsq;
        double h = Math.sqrt((1.0D + this.q * sdsq) / (1.0D + this.w * sdsq)) * ((1.0D + this.w * sdsq) / (d__1 * d__1) - this.p22 * this.ca);
        double sq = Math.sqrt(this.xj * this.xj + s * s);
        double fc;
        this.b += fc = mult * (h * this.xj - s * s) / sq;
        this.a2 += fc * Math.cos(lam + lam);
        this.a4 += fc * Math.cos(lam * 4.0D);
        fc = mult * s * (h + this.xj) / sq;
        this.c1 += fc * Math.cos(lam);
        this.c3 += fc * Math.cos(lam * 3.0D);
    }

    public void initialize() {
        super.initialize();
        byte land = 1;
        if(land > 0 && land <= 5) {
            byte path = 120;
            if(path > 0 && path <= (land <= 3?251:233)) {
                double alf;
                if(land <= 3) {
                    this.projectionLongitude = 2.2492058070450924D - 0.025032610785576042D * (double)path;
                    this.p22 = 103.2669323D;
                    alf = 1.729481662386221D;
                } else {
                    this.projectionLongitude = 2.2567107228286685D - 0.026966460545835135D * (double)path;
                    this.p22 = 98.8841202D;
                    alf = 1.7139133254584316D;
                }

                this.p22 /= 1440.0D;
                this.sa = Math.sin(alf);
                this.ca = Math.cos(alf);
                if(Math.abs(this.ca) < 1.0E-9D) {
                    this.ca = 1.0E-9D;
                }

                double esc = this.es * this.ca * this.ca;
                double ess = this.es * this.sa * this.sa;
                this.w = (1.0D - esc) * this.rone_es;
                this.w = this.w * this.w - 1.0D;
                this.q = ess * this.rone_es;
                this.t = ess * (2.0D - this.es) * this.rone_es * this.rone_es;
                this.u = esc * this.rone_es;
                this.xj = this.one_es * this.one_es * this.one_es;
                this.rlm = 1.6341348883592068D;
                this.rlm2 = this.rlm + 6.283185307179586D;
                this.a2 = this.a4 = this.b = this.c1 = this.c3 = 0.0D;
                this.seraz0(0.0D, 1.0D);

                double lam;
                for(lam = 9.0D; lam <= 81.0001D; lam += 18.0D) {
                    this.seraz0(lam, 4.0D);
                }

                for(lam = 18.0D; lam <= 72.0001D; lam += 18.0D) {
                    this.seraz0(lam, 2.0D);
                }

                this.seraz0(90.0D, 1.0D);
                this.a2 /= 30.0D;
                this.a4 /= 60.0D;
                this.b /= 30.0D;
                this.c1 /= 15.0D;
                this.c3 /= 45.0D;
            } else {
                throw new ProjectionException("-29");
            }
        } else {
            throw new ProjectionException("-28");
        }
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Landsat";
    }
}
