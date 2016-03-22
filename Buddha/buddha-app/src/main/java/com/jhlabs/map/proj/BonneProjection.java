package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class BonneProjection extends Projection {
    private double phi1;
    private double cphi1;
    private double am1;
    private double m1;
    private double[] en;

    public BonneProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double rh;
        double E;
        if(this.spherical) {
            E = this.cphi1 + this.phi1 - lpphi;
            if(Math.abs(E) > 1.0E-10D) {
                out.x = E * Math.sin(rh = lplam * Math.cos(lpphi) / E);
                out.y = this.cphi1 - E * Math.cos(rh);
            } else {
                out.x = out.y = 0.0D;
            }
        } else {
            double c;
            rh = this.am1 + this.m1 - MapMath.mlfn(lpphi, E = Math.sin(lpphi), c = Math.cos(lpphi), this.en);
            E = c * lplam / (rh * Math.sqrt(1.0D - this.es * E * E));
            out.x = rh * Math.sin(E);
            out.y = this.am1 - rh * Math.cos(E);
        }

        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double s;
        if(this.spherical) {
            s = MapMath.distance(xyx, out.y = this.cphi1 - xyy);
            out.y = this.cphi1 + this.phi1 - s;
            if(Math.abs(out.y) > 1.5707963267948966D) {
                throw new ProjectionException("I");
            }

            if(Math.abs(Math.abs(out.y) - 1.5707963267948966D) <= 1.0E-10D) {
                out.x = 0.0D;
            } else {
                out.x = s * Math.atan2(xyx, xyy) / Math.cos(out.y);
            }
        } else {
            double rh = MapMath.distance(xyx, out.y = this.am1 - xyy);
            out.y = MapMath.inv_mlfn(this.am1 + this.m1 - rh, this.es, this.en);
            if((s = Math.abs(out.y)) < 1.5707963267948966D) {
                s = Math.sin(out.y);
                out.x = rh * Math.atan2(xyx, xyy) * Math.sqrt(1.0D - this.es * s * s) / Math.cos(out.y);
            } else {
                if(Math.abs(s - 1.5707963267948966D) > 1.0E-10D) {
                    throw new ProjectionException("I");
                }

                out.x = 0.0D;
            }
        }

        return out;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();
        this.phi1 = 1.5707963267948966D;
        if(Math.abs(this.phi1) < 1.0E-10D) {
            throw new ProjectionException("-23");
        } else {
            if(!this.spherical) {
                this.en = MapMath.enfn(this.es);
                double c;
                this.m1 = MapMath.mlfn(this.phi1, this.am1 = Math.sin(this.phi1), c = Math.cos(this.phi1), this.en);
                this.am1 = c / (Math.sqrt(1.0D - this.es * this.am1 * this.am1) * this.am1);
            } else if(Math.abs(this.phi1) + 1.0E-10D >= 1.5707963267948966D) {
                this.cphi1 = 0.0D;
            } else {
                this.cphi1 = 1.0D / Math.tan(this.phi1);
            }

        }
    }

    public String toString() {
        return "Bonne";
    }
}
