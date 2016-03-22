package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.AzimuthalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class GnomonicAzimuthalProjection extends AzimuthalProjection {
    public GnomonicAzimuthalProjection() {
        this(Math.toRadians(90.0D), Math.toRadians(0.0D));
    }

    public GnomonicAzimuthalProjection(double projectionLatitude, double projectionLongitude) {
        super(projectionLatitude, projectionLongitude);
        this.minLatitude = Math.toRadians(0.0D);
        this.maxLatitude = Math.toRadians(90.0D);
        this.initialize();
    }

    public void initialize() {
        super.initialize();
    }

    public Double project(double lam, double phi, Double xy) {
        double sinphi = Math.sin(phi);
        double cosphi = Math.cos(phi);
        double coslam = Math.cos(lam);
        switch(this.mode) {
            case 1:
                xy.y = sinphi;
                break;
            case 2:
                xy.y = -sinphi;
                break;
            case 3:
                xy.y = cosphi * coslam;
                break;
            case 4:
                xy.y = this.sinphi0 * sinphi + this.cosphi0 * cosphi * coslam;
        }

        if(Math.abs(xy.y) <= 1.0E-10D) {
            throw new ProjectionException();
        } else {
            xy.x = (xy.y = 1.0D / xy.y) * cosphi * Math.sin(lam);
            switch(this.mode) {
                case 1:
                    coslam = -coslam;
                case 2:
                    xy.y *= cosphi * coslam;
                    break;
                case 3:
                    xy.y *= sinphi;
                    break;
                case 4:
                    xy.y *= this.cosphi0 * sinphi - this.sinphi0 * cosphi * coslam;
            }

            return xy;
        }
    }

    public Double projectInverse(double x, double y, Double lp) {
        double rh = MapMath.distance(x, y);
        double sinz = Math.sin(lp.y = Math.atan(rh));
        double cosz = Math.sqrt(1.0D - sinz * sinz);
        if(Math.abs(rh) <= 1.0E-10D) {
            lp.y = this.projectionLatitude;
            lp.x = 0.0D;
        } else {
            switch(this.mode) {
                case 1:
                    lp.y = 1.5707963267948966D - lp.y;
                    y = -y;
                    break;
                case 2:
                    --lp.y;
                    break;
                case 3:
                    lp.y = y * sinz / rh;
                    if(Math.abs(lp.y) >= 1.0D) {
                        lp.y = lp.y > 0.0D?1.5707963267948966D:-1.5707963267948966D;
                    } else {
                        lp.y = Math.asin(lp.y);
                    }

                    y = cosz * rh;
                    x *= sinz;
                    break;
                case 4:
                    lp.y = cosz * this.sinphi0 + y * sinz * this.cosphi0 / rh;
                    if(Math.abs(lp.y) >= 1.0D) {
                        lp.y = lp.y > 0.0D?1.5707963267948966D:-1.5707963267948966D;
                    } else {
                        lp.y = Math.asin(lp.y);
                    }

                    y = (cosz - this.sinphi0 * Math.sin(lp.y)) * rh;
                    x *= sinz * this.cosphi0;
            }

            lp.x = Math.atan2(x, y);
        }

        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Gnomonic Azimuthal";
    }
}
