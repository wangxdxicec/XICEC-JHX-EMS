package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class PolyconicProjection extends Projection {
    private double ml0;
    private double[] en;
    private static final double TOL = 1.0E-10D;
    private static final double CONV = 1.0E-10D;
    private static final int N_ITER = 10;
    private static final int I_ITER = 20;
    private static final double ITOL = 1.0E-12D;

    public PolyconicProjection() {
        this.minLatitude = MapMath.degToRad(0.0D);
        this.maxLatitude = MapMath.degToRad(80.0D);
        this.minLongitude = MapMath.degToRad(-60.0D);
        this.maxLongitude = MapMath.degToRad(60.0D);
        this.initialize();
    }

    public Double project(double lplam, double lpphi, Double out) {
        double ms;
        double sp;
        if(this.spherical) {
            if(Math.abs(lpphi) <= 1.0E-10D) {
                out.x = lplam;
                out.y = this.ml0;
            } else {
                ms = 1.0D / Math.tan(lpphi);
                out.x = Math.sin(sp = lplam * Math.sin(lpphi)) * ms;
                out.y = lpphi - this.projectionLatitude + ms * (1.0D - Math.cos(sp));
            }
        } else if(Math.abs(lpphi) <= 1.0E-10D) {
            out.x = lplam;
            out.y = -this.ml0;
        } else {
            sp = Math.sin(lpphi);
            double cp;
            ms = Math.abs(cp = Math.cos(lpphi)) > 1.0E-10D?MapMath.msfn(sp, cp, this.es) / sp:0.0D;
            out.x = ms * Math.sin(out.x *= sp);
            out.y = MapMath.mlfn(lpphi, sp, cp, this.en) - this.ml0 + ms * (1.0D - Math.cos(lplam));
        }

        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double lpphi;
        double r;
        double c;
        double sp;
        if(this.spherical) {
            if(Math.abs(this.projectionLatitude + xyy) <= 1.0E-10D) {
                out.x = xyx;
                out.y = 0.0D;
            } else {
                lpphi = xyy;
                r = xyx * xyx + xyy * xyy;
                int cp = 10;

                do {
                    sp = Math.tan(lpphi);
                    out.y -= c = (xyy * (lpphi * sp + 1.0D) - lpphi - 0.5D * (lpphi * lpphi + r) * sp) / ((lpphi - xyy) / sp - 1.0D);
                    if(Math.abs(c) <= 1.0E-10D) {
                        break;
                    }

                    --cp;
                } while(cp > 0);

                if(cp == 0) {
                    throw new ProjectionException("I");
                }

                out.x = Math.asin(xyx * Math.tan(lpphi)) / Math.sin(lpphi);
                out.y = lpphi;
            }
        } else {
            xyy += this.ml0;
            if(Math.abs(xyy) <= 1.0E-10D) {
                out.x = xyx;
                out.y = 0.0D;
            } else {
                r = xyy * xyy + xyx * xyx;
                lpphi = xyy;

                int i;
                for(i = 20; i > 0; --i) {
                    sp = Math.sin(lpphi);
                    double var27;
                    double s2ph = sp * (var27 = Math.cos(lpphi));
                    if(Math.abs(var27) < 1.0E-12D) {
                        throw new ProjectionException("I");
                    }

                    double mlp;
                    c = sp * (mlp = Math.sqrt(1.0D - this.es * sp * sp)) / var27;
                    double ml = MapMath.mlfn(lpphi, sp, var27, this.en);
                    double mlb = ml * ml + r;
                    mlp = 1.0D / this.es / (mlp * mlp * mlp);
                    double dPhi;
                    lpphi += dPhi = (ml + ml + c * mlb - 2.0D * xyy * (c * ml + 1.0D)) / (this.es * s2ph * (mlb - 2.0D * xyy * ml) / c + 2.0D * (xyy - ml) * (c * mlp - 1.0D / s2ph) - mlp - mlp);
                    if(Math.abs(dPhi) <= 1.0E-12D) {
                        break;
                    }
                }

                if(i == 0) {
                    throw new ProjectionException("I");
                }

                c = Math.sin(lpphi);
                out.x = Math.asin(xyx * Math.tan(lpphi) * Math.sqrt(1.0D - this.es * c * c)) / Math.sin(lpphi);
                out.y = lpphi;
            }
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();
        this.spherical = true;
        if(!this.spherical) {
            this.en = MapMath.enfn(this.es);
            if(this.en == null) {
                throw new ProjectionException("E");
            }

            this.ml0 = MapMath.mlfn(this.projectionLatitude, Math.sin(this.projectionLatitude), Math.cos(this.projectionLatitude), this.en);
        } else {
            this.ml0 = -this.projectionLatitude;
        }

    }

    public String toString() {
        return "Polyconic (American)";
    }
}
