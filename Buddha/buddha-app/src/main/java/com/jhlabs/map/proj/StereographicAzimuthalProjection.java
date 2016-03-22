package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.AzimuthalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class StereographicAzimuthalProjection extends AzimuthalProjection {
    private static final double TOL = 1.0E-8D;
    private double akm1;

    public StereographicAzimuthalProjection() {
        this(Math.toRadians(90.0D), Math.toRadians(0.0D));
    }

    public StereographicAzimuthalProjection(double projectionLatitude, double projectionLongitude) {
        super(projectionLatitude, projectionLongitude);
        this.initialize();
    }

    public void setupUPS(int pole) {
        this.projectionLatitude = pole == 2?-1.5707963267948966D:1.5707963267948966D;
        this.projectionLongitude = 0.0D;
        this.scaleFactor = 0.994D;
        this.falseEasting = 2000000.0D;
        this.falseNorthing = 2000000.0D;
        this.trueScaleLatitude = 1.5707963267948966D;
        this.initialize();
    }

    public void initialize() {
        super.initialize();
        double t;
        if(Math.abs((t = Math.abs(this.projectionLatitude)) - 1.5707963267948966D) < 1.0E-10D) {
            this.mode = this.projectionLatitude < 0.0D?2:1;
        } else {
            this.mode = t > 1.0E-10D?4:3;
        }

        this.trueScaleLatitude = Math.abs(this.trueScaleLatitude);
        if(this.spherical) {
            switch(this.mode) {
                case 1:
                case 2:
                    if(Math.abs(this.trueScaleLatitude - 1.5707963267948966D) < 1.0E-10D) {
                        this.akm1 = 2.0D * this.scaleFactor / Math.sqrt(Math.pow(1.0D + this.e, 1.0D + this.e) * Math.pow(1.0D - this.e, 1.0D - this.e));
                    } else {
                        this.akm1 = Math.cos(this.trueScaleLatitude) / MapMath.tsfn(this.trueScaleLatitude, t = Math.sin(this.trueScaleLatitude), this.e);
                        t *= this.e;
                        this.akm1 /= Math.sqrt(1.0D - t * t);
                    }
                    break;
                case 3:
                    this.akm1 = 2.0D * this.scaleFactor;
                    break;
                case 4:
                    t = Math.sin(this.projectionLatitude);
                    double X = 2.0D * Math.atan(this.ssfn(this.projectionLatitude, t, this.e)) - 1.5707963267948966D;
                    t *= this.e;
                    this.akm1 = 2.0D * this.scaleFactor * Math.cos(this.projectionLatitude) / Math.sqrt(1.0D - t * t);
                    this.sinphi0 = Math.sin(X);
                    this.cosphi0 = Math.cos(X);
            }
        } else {
            switch(this.mode) {
                case 1:
                case 2:
                    this.akm1 = Math.abs(this.trueScaleLatitude - 1.5707963267948966D) >= 1.0E-10D?Math.cos(this.trueScaleLatitude) / Math.tan(0.7853981633974483D - 0.5D * this.trueScaleLatitude):2.0D * this.scaleFactor;
                    break;
                case 4:
                    this.sinphi0 = Math.sin(this.projectionLatitude);
                    this.cosphi0 = Math.cos(this.projectionLatitude);
                case 3:
                    this.akm1 = 2.0D * this.scaleFactor;
            }
        }

    }

    public Double project(double lam, double phi, Double xy) {
        double coslam = Math.cos(lam);
        double sinlam = Math.sin(lam);
        double sinphi = Math.sin(phi);
        double sinX;
        if(this.spherical) {
            sinX = Math.cos(phi);
            switch(this.mode) {
                case 1:
                    coslam = -coslam;
                    phi = -phi;
                case 2:
                    if(Math.abs(phi - 1.5707963267948966D) < 1.0E-8D) {
                        throw new ProjectionException();
                    }

                    xy.x = sinlam * (xy.y = this.akm1 * Math.tan(0.7853981633974483D + 0.5D * phi));
                    xy.y *= coslam;
                    break;
                case 3:
                    xy.y = 1.0D + sinX * coslam;
                    if(xy.y <= 1.0E-10D) {
                        throw new ProjectionException();
                    }

                    xy.x = (xy.y = this.akm1 / xy.y) * sinX * sinlam;
                    xy.y *= sinphi;
                    break;
                case 4:
                    xy.y = 1.0D + this.sinphi0 * sinphi + this.cosphi0 * sinX * coslam;
                    if(xy.y <= 1.0E-10D) {
                        throw new ProjectionException();
                    }

                    xy.x = (xy.y = this.akm1 / xy.y) * sinX * sinlam;
                    xy.y *= this.cosphi0 * sinphi - this.sinphi0 * sinX * coslam;
            }
        } else {
            sinX = 0.0D;
            double cosX = 0.0D;
            if(this.mode == 4 || this.mode == 3) {
                double X;
                sinX = Math.sin(X = 2.0D * Math.atan(this.ssfn(phi, sinphi, this.e)) - 1.5707963267948966D);
                cosX = Math.cos(X);
            }

            double A;
            switch(this.mode) {
                case 2:
                    phi = -phi;
                    coslam = -coslam;
                    sinphi = -sinphi;
                case 1:
                    xy.x = this.akm1 * MapMath.tsfn(phi, sinphi, this.e);
                    xy.y = -xy.x * coslam;
                    break;
                case 3:
                    A = 2.0D * this.akm1 / (1.0D + cosX * coslam);
                    xy.y = A * sinX;
                    xy.x = A * cosX;
                    break;
                case 4:
                    A = this.akm1 / (this.cosphi0 * (1.0D + this.sinphi0 * sinX + this.cosphi0 * cosX * coslam));
                    xy.y = A * (this.cosphi0 * sinX - this.sinphi0 * cosX * coslam);
                    xy.x = A * cosX;
            }

            xy.x *= sinlam;
        }

        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        double cosphi;
        double sinphi;
        double tp;
        double phi_l;
        if(this.spherical) {
            tp = Math.sin(cosphi = 2.0D * Math.atan((sinphi = MapMath.distance(x, y)) / this.akm1));
            phi_l = Math.cos(cosphi);
            lp.x = 0.0D;
            switch(this.mode) {
                case 1:
                    y = -y;
                case 2:
                    if(Math.abs(sinphi) <= 1.0E-10D) {
                        lp.y = this.projectionLatitude;
                    } else {
                        lp.y = Math.asin(this.mode == 2?-phi_l:phi_l);
                    }

                    lp.x = x == 0.0D && y == 0.0D?0.0D:Math.atan2(x, y);
                    break;
                case 3:
                    if(Math.abs(sinphi) <= 1.0E-10D) {
                        lp.y = 0.0D;
                    } else {
                        lp.y = Math.asin(y * tp / sinphi);
                    }

                    if(phi_l != 0.0D || x != 0.0D) {
                        lp.x = Math.atan2(x * tp, phi_l * sinphi);
                    }
                    break;
                case 4:
                    if(Math.abs(sinphi) <= 1.0E-10D) {
                        lp.y = this.projectionLatitude;
                    } else {
                        lp.y = Math.asin(phi_l * this.sinphi0 + y * tp * this.cosphi0 / sinphi);
                    }

                    if((cosphi = phi_l - this.sinphi0 * Math.sin(lp.y)) != 0.0D || x != 0.0D) {
                        lp.x = Math.atan2(x * tp * this.cosphi0, cosphi * sinphi);
                    }
            }

            return lp;
        } else {
            double rho = MapMath.distance(x, y);
            double halfe;
            double halfpi;
            switch(this.mode) {
                case 1:
                    y = -y;
                case 2:
                    phi_l = 1.5707963267948966D - 2.0D * Math.atan(tp = -rho / this.akm1);
                    halfpi = -1.5707963267948966D;
                    halfe = -0.5D * this.e;
                    break;
                case 3:
                case 4:
                default:
                    cosphi = Math.cos(tp = 2.0D * Math.atan2(rho * this.cosphi0, this.akm1));
                    sinphi = Math.sin(tp);
                    phi_l = Math.asin(cosphi * this.sinphi0 + y * sinphi * this.cosphi0 / rho);
                    tp = Math.tan(0.5D * (1.5707963267948966D + phi_l));
                    x *= sinphi;
                    y = rho * this.cosphi0 * cosphi - y * this.sinphi0 * sinphi;
                    halfpi = 1.5707963267948966D;
                    halfe = 0.5D * this.e;
            }

            for(int i = 8; i-- != 0; phi_l = lp.y) {
                sinphi = this.e * Math.sin(phi_l);
                lp.y = 2.0D * Math.atan(tp * Math.pow((1.0D + sinphi) / (1.0D - sinphi), halfe)) - halfpi;
                if(Math.abs(phi_l - lp.y) < 1.0E-10D) {
                    if(this.mode == 2) {
                        lp.y = -lp.y;
                    }

                    lp.x = x == 0.0D && y == 0.0D?0.0D:Math.atan2(x, y);
                    return lp;
                }
            }

            throw new RuntimeException("Iteration didn\'t converge");
        }
    }

    public boolean isConformal() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    private double ssfn(double phit, double sinphi, double eccen) {
        sinphi *= eccen;
        return Math.tan(0.5D * (1.5707963267948966D + phit)) * Math.pow((1.0D - sinphi) / (1.0D + sinphi), 0.5D * eccen);
    }

    public String toString() {
        return "Stereographic Azimuthal";
    }
}
