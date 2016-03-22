package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.ConicProjection;
import com.jhlabs.map.proj.Ellipsoid;
import com.jhlabs.map.proj.ProjectionException;

public class LambertConformalConicProjection extends ConicProjection {
    private double n;
    private double rho0;
    private double c;

    public LambertConformalConicProjection() {
        this.minLatitude = Math.toRadians(0.0D);
        this.maxLatitude = Math.toRadians(80.0D);
        this.projectionLatitude = 0.7853981633974483D;
        this.projectionLatitude1 = 0.0D;
        this.projectionLatitude2 = 0.0D;
        this.initialize();
    }

    public LambertConformalConicProjection(Ellipsoid ellipsoid, double lon_0, double lat_1, double lat_2, double lat_0, double x_0, double y_0) {
        this.setEllipsoid(ellipsoid);
        this.projectionLongitude = lon_0;
        this.projectionLatitude = lat_0;
        this.scaleFactor = 1.0D;
        this.falseEasting = x_0;
        this.falseNorthing = y_0;
        this.projectionLatitude1 = lat_1;
        this.projectionLatitude2 = lat_2;
        this.initialize();
    }

    public Double project(double x, double y, Double out) {
        double rho;
        if(Math.abs(Math.abs(y) - 1.5707963267948966D) < 1.0E-10D) {
            rho = 0.0D;
        } else {
            rho = this.c * (this.spherical?Math.pow(Math.tan(0.7853981633974483D + 0.5D * y), -this.n):Math.pow(MapMath.tsfn(y, Math.sin(y), this.e), this.n));
        }

        out.x = this.scaleFactor * rho * Math.sin(x *= this.n);
        out.y = this.scaleFactor * (this.rho0 - rho * Math.cos(x));
        return out;
    }

    public Double projectInverse(double x, double y, Double out) {
        x /= this.scaleFactor;
        y /= this.scaleFactor;
        double rho = MapMath.distance(x, y = this.rho0 - y);
        if(rho != 0.0D) {
            if(this.n < 0.0D) {
                rho = -rho;
                x = -x;
                y = -y;
            }

            if(this.spherical) {
                out.y = 2.0D * Math.atan(Math.pow(this.c / rho, 1.0D / this.n)) - 1.5707963267948966D;
            } else {
                out.y = MapMath.phi2(Math.pow(rho / this.c, 1.0D / this.n), this.e);
            }

            out.x = Math.atan2(x, y) / this.n;
        } else {
            out.x = 0.0D;
            out.y = this.n > 0.0D?1.5707963267948966D:-1.5707963267948966D;
        }

        return out;
    }

    public void initialize() {
        super.initialize();
        if(this.projectionLatitude1 == 0.0D) {
            this.projectionLatitude1 = this.projectionLatitude2 = this.projectionLatitude;
        }

        if(Math.abs(this.projectionLatitude1 + this.projectionLatitude2) < 1.0E-10D) {
            throw new ProjectionException();
        } else {
            double sinphi;
            this.n = sinphi = Math.sin(this.projectionLatitude1);
            double cosphi = Math.cos(this.projectionLatitude1);
            boolean secant = Math.abs(this.projectionLatitude1 - this.projectionLatitude2) >= 1.0E-10D;
            this.spherical = this.es == 0.0D;
            if(!this.spherical) {
                double m1 = MapMath.msfn(sinphi, cosphi, this.es);
                double ml1 = MapMath.tsfn(this.projectionLatitude1, sinphi, this.e);
                if(secant) {
                    this.n = Math.log(m1 / MapMath.msfn(sinphi = Math.sin(this.projectionLatitude2), Math.cos(this.projectionLatitude2), this.es));
                    this.n /= Math.log(ml1 / MapMath.tsfn(this.projectionLatitude2, sinphi, this.e));
                }

                this.c = this.rho0 = m1 * Math.pow(ml1, -this.n) / this.n;
                this.rho0 *= Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) < 1.0E-10D?0.0D:Math.pow(MapMath.tsfn(this.projectionLatitude, Math.sin(this.projectionLatitude), this.e), this.n);
            } else {
                if(secant) {
                    this.n = Math.log(cosphi / Math.cos(this.projectionLatitude2)) / Math.log(Math.tan(0.7853981633974483D + 0.5D * this.projectionLatitude2) / Math.tan(0.7853981633974483D + 0.5D * this.projectionLatitude1));
                }

                this.c = cosphi * Math.pow(Math.tan(0.7853981633974483D + 0.5D * this.projectionLatitude1), this.n) / this.n;
                this.rho0 = Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) < 1.0E-10D?0.0D:this.c * Math.pow(Math.tan(0.7853981633974483D + 0.5D * this.projectionLatitude), -this.n);
            }

        }
    }

    public boolean isConformal() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Lambert Conformal Conic";
    }
}
