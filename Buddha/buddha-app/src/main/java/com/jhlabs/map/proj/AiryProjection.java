package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class AiryProjection extends Projection {
    private double p_halfpi;
    private double sinph0;
    private double cosph0;
    private double Cb;
    private int mode;
    private boolean no_cut = true;
    private static final double EPS = 1.0E-10D;
    private static final int N_POLE = 0;
    private static final int S_POLE = 1;
    private static final int EQUIT = 2;
    private static final int OBLIQ = 3;

    public AiryProjection() {
        this.minLatitude = Math.toRadians(-60.0D);
        this.maxLatitude = Math.toRadians(60.0D);
        this.minLongitude = Math.toRadians(-90.0D);
        this.maxLongitude = Math.toRadians(90.0D);
        this.initialize();
    }

    public Double project(double lplam, double lpphi, Double out) {
        double sinlam = Math.sin(lplam);
        double coslam = Math.cos(lplam);
        double Krho;
        double t;
        switch(this.mode) {
            case 0:
            case 1:
                out.y = Math.abs(this.p_halfpi - lpphi);
                if(!this.no_cut && lpphi - 1.0E-10D > 1.5707963267948966D) {
                    throw new ProjectionException("F");
                }

                if((out.y *= 0.5D) > 1.0E-10D) {
                    t = Math.tan(lpphi);
                    Krho = -2.0D * (Math.log(Math.cos(lpphi)) / t + t * this.Cb);
                    out.x = Krho * sinlam;
                    out.y = Krho * coslam;
                    if(this.mode == 0) {
                        out.y = -out.y;
                    }
                } else {
                    out.x = out.y = 0.0D;
                }
                break;
            case 2:
            case 3:
                double sinphi = Math.sin(lpphi);
                double cosphi = Math.cos(lpphi);
                double cosz = cosphi * coslam;
                if(this.mode == 3) {
                    cosz = this.sinph0 * sinphi + this.cosph0 * cosz;
                }

                if(!this.no_cut && cosz < -1.0E-10D) {
                    throw new ProjectionException("F");
                }

                double s = 1.0D - cosz;
                if(Math.abs(s) > 1.0E-10D) {
                    t = 0.5D * (1.0D + cosz);
                    Krho = -Math.log(t) / s - this.Cb / t;
                } else {
                    Krho = 0.5D - this.Cb;
                }

                out.x = Krho * cosphi * sinlam;
                if(this.mode == 3) {
                    out.y = Krho * (this.cosph0 * sinphi - this.sinph0 * cosphi * coslam);
                } else {
                    out.y = Krho * sinphi;
                }
        }

        return out;
    }

    public void initialize() {
        super.initialize();
        this.no_cut = false;
        double beta = 0.7853981633974483D;
        if(Math.abs(beta) < 1.0E-10D) {
            this.Cb = -0.5D;
        } else {
            this.Cb = 1.0D / Math.tan(beta);
            this.Cb *= this.Cb * Math.log(Math.cos(beta));
        }

        if(Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) < 1.0E-10D) {
            if(this.projectionLatitude < 0.0D) {
                this.p_halfpi = -1.5707963267948966D;
                this.mode = 1;
            } else {
                this.p_halfpi = 1.5707963267948966D;
                this.mode = 0;
            }
        } else if(Math.abs(this.projectionLatitude) < 1.0E-10D) {
            this.mode = 2;
        } else {
            this.mode = 3;
            this.sinph0 = Math.sin(this.projectionLatitude);
            this.cosph0 = Math.cos(this.projectionLatitude);
        }

    }

    public String toString() {
        return "Airy";
    }
}
