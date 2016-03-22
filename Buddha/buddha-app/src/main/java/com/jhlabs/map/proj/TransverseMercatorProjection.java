package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.CylindricalProjection;
import com.jhlabs.map.proj.Ellipsoid;

public class TransverseMercatorProjection extends CylindricalProjection {
    private static final double FC1 = 1.0D;
    private static final double FC2 = 0.5D;
    private static final double FC3 = 0.16666666666666666D;
    private static final double FC4 = 0.08333333333333333D;
    private static final double FC5 = 0.05D;
    private static final double FC6 = 0.03333333333333333D;
    private static final double FC7 = 0.023809523809523808D;
    private static final double FC8 = 0.017857142857142856D;
    private double esp;
    private double ml0;
    private double[] en;

    public TransverseMercatorProjection() {
        this.ellipsoid = Ellipsoid.GRS_1980;
        this.projectionLatitude = Math.toRadians(0.0D);
        this.projectionLongitude = Math.toRadians(0.0D);
        this.minLongitude = Math.toRadians(-90.0D);
        this.maxLongitude = Math.toRadians(90.0D);
        this.initialize();
    }

    public TransverseMercatorProjection(Ellipsoid ellipsoid, double lon_0, double lat_0, double k, double x_0, double y_0) {
        this.setEllipsoid(ellipsoid);
        this.projectionLongitude = lon_0;
        this.projectionLatitude = lat_0;
        this.scaleFactor = k;
        this.falseEasting = x_0;
        this.falseNorthing = y_0;
        this.initialize();
    }

    public Object clone() {
        TransverseMercatorProjection p = (TransverseMercatorProjection)super.clone();
        if(this.en != null) {
            p.en = (double[])this.en.clone();
        }

        return p;
    }

    public boolean isRectilinear() {
        return false;
    }

    public void initialize() {
        super.initialize();
        if(this.spherical) {
            this.esp = this.scaleFactor;
            this.ml0 = 0.5D * this.esp;
        } else {
            this.en = MapMath.enfn(this.es);
            this.ml0 = MapMath.mlfn(this.projectionLatitude, Math.sin(this.projectionLatitude), Math.cos(this.projectionLatitude), this.en);
            this.esp = this.es / (1.0D - this.es);
        }

    }

    public int getRowFromNearestParallel(double latitude) {
        int degrees = (int)MapMath.radToDeg(MapMath.normalizeLatitude(latitude));
        return degrees >= -80 && degrees <= 84?(degrees > 80?24:(degrees + 80) / 8 + 3):0;
    }

    public int getZoneFromNearestMeridian(double longitude) {
        int zone = (int)Math.floor((MapMath.normalizeLongitude(longitude) + 3.141592653589793D) * 30.0D / 3.141592653589793D) + 1;
        if(zone < 1) {
            zone = 1;
        } else if(zone > 60) {
            zone = 60;
        }

        return zone;
    }

    public void setUTMZone(int zone) {
        --zone;
        this.projectionLongitude = ((double)zone + 0.5D) * 3.141592653589793D / 30.0D - 3.141592653589793D;
        this.projectionLatitude = 0.0D;
        this.scaleFactor = 0.9996D;
        this.falseEasting = 500000.0D;
        this.initialize();
    }

    public Double project(double lplam, double lpphi, Double xy) {
        double al;
        double als;
        double n;
        if(this.spherical) {
            al = Math.cos(lpphi);
            als = al * Math.sin(lplam);
            xy.x = this.ml0 * this.scaleFactor * Math.log((1.0D + als) / (1.0D - als));
            n = al * Math.cos(lplam) / Math.sqrt(1.0D - als * als);
            n = MapMath.acos(n);
            if(lpphi < 0.0D) {
                n = -n;
            }

            xy.y = this.esp * (n - this.projectionLatitude);
        } else {
            double sinphi = Math.sin(lpphi);
            double cosphi = Math.cos(lpphi);
            double t = Math.abs(cosphi) > 1.0E-10D?sinphi / cosphi:0.0D;
            t *= t;
            al = cosphi * lplam;
            als = al * al;
            al /= Math.sqrt(1.0D - this.es * sinphi * sinphi);
            n = this.esp * cosphi * cosphi;
            xy.x = this.scaleFactor * al * (1.0D + 0.16666666666666666D * als * (1.0D - t + n + 0.05D * als * (5.0D + t * (t - 18.0D) + n * (14.0D - 58.0D * t) + 0.023809523809523808D * als * (61.0D + t * (t * (179.0D - t) - 479.0D)))));
            xy.y = this.scaleFactor * (MapMath.mlfn(lpphi, sinphi, cosphi, this.en) - this.ml0 + sinphi * al * lplam * 0.5D * (1.0D + 0.08333333333333333D * als * (5.0D - t + n * (9.0D + 4.0D * n) + 0.03333333333333333D * als * (61.0D + t * (t - 58.0D) + n * (270.0D - 330.0D * t) + 0.017857142857142856D * als * (1385.0D + t * (t * (543.0D - t) - 3111.0D))))));
        }

        return xy;
    }

    public Double projectInverse(double x, double y, Double out) {
        double n;
        double con;
        if(this.spherical) {
            n = Math.exp(x / this.scaleFactor);
            con = 0.5D * (n - 1.0D / n);
            n = Math.cos(this.projectionLatitude + y / this.scaleFactor);
            out.y = MapMath.asin(Math.sqrt((1.0D - n * n) / (1.0D + con * con)));
            if(y < 0.0D) {
                out.y = -out.y;
            }

            out.x = Math.atan2(con, n);
        } else {
            out.y = MapMath.inv_mlfn(this.ml0 + y / this.scaleFactor, this.es, this.en);
            if(Math.abs(y) >= 1.5707963267948966D) {
                out.y = y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
                out.x = 0.0D;
            } else {
                double sinphi = Math.sin(out.y);
                double cosphi = Math.cos(out.y);
                double t = Math.abs(cosphi) > 1.0E-10D?sinphi / cosphi:0.0D;
                n = this.esp * cosphi * cosphi;
                double d = x * Math.sqrt(con = 1.0D - this.es * sinphi * sinphi) / this.scaleFactor;
                con *= t;
                t *= t;
                double ds = d * d;
                out.y -= con * ds / (1.0D - this.es) * 0.5D * (1.0D - ds * 0.08333333333333333D * (5.0D + t * (3.0D - 9.0D * n) + n * (1.0D - 4.0D * n) - ds * 0.03333333333333333D * (61.0D + t * (90.0D - 252.0D * n + 45.0D * t) + 46.0D * n - ds * 0.017857142857142856D * (1385.0D + t * (3633.0D + t * (4095.0D + 1574.0D * t))))));
                out.x = d * (1.0D - ds * 0.16666666666666666D * (1.0D + 2.0D * t + n - ds * 0.05D * (5.0D + t * (28.0D + 24.0D * t + 8.0D * n) + 6.0D * n - ds * 0.023809523809523808D * (61.0D + t * (662.0D + t * (1320.0D + 720.0D * t)))))) / cosphi;
            }
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Transverse Mercator";
    }
}
