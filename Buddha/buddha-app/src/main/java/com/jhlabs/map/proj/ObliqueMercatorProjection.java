package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Ellipsoid;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class ObliqueMercatorProjection extends Projection {
    private static final double TOL = 1.0E-7D;
    private double alpha;
    private double lamc;
    private double lam1;
    private double phi1;
    private double lam2;
    private double phi2;
    private double Gamma;
    private double al;
    private double bl;
    private double el;
    private double singam;
    private double cosgam;
    private double sinrot;
    private double cosrot;
    private double u_0;
    private boolean ellips;
    private boolean rot;

    public ObliqueMercatorProjection() {
        this.ellipsoid = Ellipsoid.WGS_1984;
        this.projectionLatitude = Math.toRadians(0.0D);
        this.projectionLongitude = Math.toRadians(0.0D);
        this.minLongitude = Math.toRadians(-60.0D);
        this.maxLongitude = Math.toRadians(60.0D);
        this.minLatitude = Math.toRadians(-80.0D);
        this.maxLatitude = Math.toRadians(80.0D);
        this.alpha = Math.toRadians(-45.0D);
        this.initialize();
    }

    public ObliqueMercatorProjection(Ellipsoid ellipsoid, double lon_0, double lat_0, double alpha, double k, double x_0, double y_0) {
        this.setEllipsoid(ellipsoid);
        this.lamc = lon_0;
        this.projectionLatitude = lat_0;
        this.alpha = alpha;
        this.scaleFactor = k;
        this.falseEasting = x_0;
        this.falseNorthing = y_0;
        this.initialize();
    }

    public void initialize() {
        super.initialize();
        boolean azi = true;
        this.rot = true;
        double con;
        if(azi) {
            if(Math.abs(this.alpha) <= 1.0E-7D || Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) <= 1.0E-7D || Math.abs(Math.abs(this.alpha) - 1.5707963267948966D) <= 1.0E-7D) {
                throw new ProjectionException("Obl 1");
            }
        } else if(Math.abs(this.phi1 - this.phi2) <= 1.0E-7D || (con = Math.abs(this.phi1)) <= 1.0E-7D || Math.abs(con - 1.5707963267948966D) <= 1.0E-7D || Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) <= 1.0E-7D || Math.abs(Math.abs(this.phi2) - 1.5707963267948966D) <= 1.0E-7D) {
            throw new ProjectionException("Obl 2");
        }

        double com = (this.spherical = this.es == 0.0D)?1.0D:Math.sqrt(this.one_es);
        double d;
        double f;
        if(Math.abs(this.projectionLatitude) > 1.0E-10D) {
            double sinphi0 = Math.sin(this.projectionLatitude);
            double cosphi0 = Math.cos(this.projectionLatitude);
            if(!this.spherical) {
                con = 1.0D - this.es * sinphi0 * sinphi0;
                this.bl = cosphi0 * cosphi0;
                this.bl = Math.sqrt(1.0D + this.es * this.bl * this.bl / this.one_es);
                this.al = this.bl * this.scaleFactor * com / con;
                d = this.bl * com / (cosphi0 * Math.sqrt(con));
            } else {
                this.bl = 1.0D;
                this.al = this.scaleFactor;
                d = 1.0D / cosphi0;
            }

            if((f = d * d - 1.0D) <= 0.0D) {
                f = 0.0D;
            } else {
                f = Math.sqrt(f);
                if(this.projectionLatitude < 0.0D) {
                    f = -f;
                }
            }

            this.el = f += d;
            if(!this.spherical) {
                this.el *= Math.pow(MapMath.tsfn(this.projectionLatitude, sinphi0, this.e), this.bl);
            } else {
                this.el *= Math.tan(0.5D * (1.5707963267948966D - this.projectionLatitude));
            }
        } else {
            this.bl = 1.0D / com;
            this.al = this.scaleFactor;
            f = 1.0D;
            d = 1.0D;
            this.el = 1.0D;
        }

        if(azi) {
            this.Gamma = Math.asin(Math.sin(this.alpha) / d);
            this.projectionLongitude = this.lamc - Math.asin(0.5D * (f - 1.0D / f) * Math.tan(this.Gamma)) / this.bl;
        } else {
            double h;
            double l;
            if(!this.spherical) {
                h = Math.pow(MapMath.tsfn(this.phi1, Math.sin(this.phi1), this.e), this.bl);
                l = Math.pow(MapMath.tsfn(this.phi2, Math.sin(this.phi2), this.e), this.bl);
            } else {
                h = Math.tan(0.5D * (1.5707963267948966D - this.phi1));
                l = Math.tan(0.5D * (1.5707963267948966D - this.phi2));
            }

            f = this.el / h;
            double p = (l - h) / (l + h);
            double j = this.el * this.el;
            j = (j - l * h) / (j + l * h);
            if((con = this.lam1 - this.lam2) < -3.141592653589793D) {
                this.lam2 -= 6.283185307179586D;
            } else if(con > 3.141592653589793D) {
                this.lam2 += 6.283185307179586D;
            }

            this.projectionLongitude = MapMath.normalizeLongitude(0.5D * (this.lam1 + this.lam2) - Math.atan(j * Math.tan(0.5D * this.bl * (this.lam1 - this.lam2)) / p) / this.bl);
            this.Gamma = Math.atan(2.0D * Math.sin(this.bl * MapMath.normalizeLongitude(this.lam1 - this.projectionLongitude)) / (f - 1.0D / f));
            this.alpha = Math.asin(d * Math.sin(this.Gamma));
        }

        this.singam = Math.sin(this.Gamma);
        this.cosgam = Math.cos(this.Gamma);
        f = this.alpha;
        this.sinrot = Math.sin(f);
        this.cosrot = Math.cos(f);
        this.u_0 = Math.abs(this.al * Math.atan(Math.sqrt(d * d - 1.0D) / this.cosrot) / this.bl);
        if(this.projectionLatitude < 0.0D) {
            this.u_0 = -this.u_0;
        }

    }

    public Double project(double lam, double phi, Double xy) {
        double vl = Math.sin(this.bl * lam);
        double ul;
        double us;
        if(Math.abs(Math.abs(phi) - 1.5707963267948966D) <= 1.0E-10D) {
            ul = phi < 0.0D?-this.singam:this.singam;
            us = this.al * phi / this.bl;
        } else {
            double q = this.el / (!this.spherical?Math.pow(MapMath.tsfn(phi, Math.sin(phi), this.e), this.bl):Math.tan(0.5D * (1.5707963267948966D - phi)));
            double s = 0.5D * (q - 1.0D / q);
            ul = 2.0D * (s * this.singam - vl * this.cosgam) / (q + 1.0D / q);
            double con = Math.cos(this.bl * lam);
            if(Math.abs(con) >= 1.0E-7D) {
                us = this.al * Math.atan((s * this.cosgam + vl * this.singam) / con) / this.bl;
                if(con < 0.0D) {
                    us += 3.141592653589793D * this.al / this.bl;
                }
            } else {
                us = this.al * this.bl * lam;
            }
        }

        if(Math.abs(Math.abs(ul) - 1.0D) <= 1.0E-10D) {
            throw new ProjectionException("Obl 3");
        } else {
            double vs = 0.5D * this.al * Math.log((1.0D - ul) / (1.0D + ul)) / this.bl;
            us -= this.u_0;
            if(!this.rot) {
                xy.x = us;
                xy.y = vs;
            } else {
                xy.x = vs * this.cosrot + us * this.sinrot;
                xy.y = us * this.cosrot - vs * this.sinrot;
            }

            return xy;
        }
    }

    public Double projectInverse(double x, double y, Double lp) {
        double vs;
        double us;
        if(!this.rot) {
            us = x;
            vs = y;
        } else {
            vs = x * this.cosrot - y * this.sinrot;
            us = y * this.cosrot + x * this.sinrot;
        }

        us += this.u_0;
        double q = Math.exp(-this.bl * vs / this.al);
        double s = 0.5D * (q - 1.0D / q);
        double vl = Math.sin(this.bl * us / this.al);
        double ul = 2.0D * (vl * this.cosgam + s * this.singam) / (q + 1.0D / q);
        if(Math.abs(Math.abs(ul) - 1.0D) < 1.0E-10D) {
            lp.x = 0.0D;
            lp.y = ul < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        } else {
            lp.y = this.el / Math.sqrt((1.0D + ul) / (1.0D - ul));
            if(!this.spherical) {
                lp.y = MapMath.phi2(Math.pow(lp.y, 1.0D / this.bl), this.e);
            } else {
                lp.y = 1.5707963267948966D - 2.0D * Math.atan(lp.y);
            }

            lp.x = -Math.atan2(s * this.cosgam - vl * this.singam, Math.cos(this.bl * us / this.al)) / this.bl;
        }

        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Oblique Mercator";
    }
}
