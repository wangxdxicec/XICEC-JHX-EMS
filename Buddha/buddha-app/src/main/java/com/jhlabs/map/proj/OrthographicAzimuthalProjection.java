package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.AzimuthalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class OrthographicAzimuthalProjection extends AzimuthalProjection {
    public OrthographicAzimuthalProjection() {
        this.initialize();
    }

    public Double project(double lam, double phi, Double xy) {
        double cosphi = Math.cos(phi);
        double coslam = Math.cos(lam);
        switch(this.mode) {
            case 1:
                coslam = -coslam;
            case 2:
                xy.y = cosphi * coslam;
                break;
            case 3:
                xy.y = Math.sin(phi);
                break;
            case 4:
                double sinphi = Math.sin(phi);
                xy.y = this.cosphi0 * sinphi - this.sinphi0 * cosphi * coslam;
        }

        xy.x = cosphi * Math.sin(lam);
        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        double rh;
        double sinc;
        if((sinc = rh = MapMath.distance(x, y)) > 1.0D) {
            if(sinc - 1.0D > 1.0E-10D) {
                throw new ProjectionException();
            }

            sinc = 1.0D;
        }

        double cosc = Math.sqrt(1.0D - sinc * sinc);
        if(Math.abs(rh) <= 1.0E-10D) {
            lp.y = this.projectionLatitude;
        } else {
            switch(this.mode) {
                case 1:
                    y = -y;
                    lp.y = Math.acos(sinc);
                    break;
                case 2:
                    lp.y = -Math.acos(sinc);
                    break;
                case 3:
                    lp.y = y * sinc / rh;
                    x *= sinc;
                    y = cosc * rh;
                    if(Math.abs(lp.y) >= 1.0D) {
                        lp.y = lp.y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
                    } else {
                        lp.y = Math.asin(lp.y);
                    }
                    break;
                case 4:
                    lp.y = cosc * this.sinphi0 + y * sinc * this.cosphi0 / rh;
                    y = (cosc - this.sinphi0 * lp.y) * rh;
                    x *= sinc * this.cosphi0;
                    if(Math.abs(lp.y) >= 1.0D) {
                        lp.y = lp.y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
                    } else {
                        lp.y = Math.asin(lp.y);
                    }
            }
        }

        lp.x = y != 0.0D || this.mode != 4 && this.mode != 3?Math.atan2(x, y):(x == 0.0D?0.0D:(x < 0.0D?-1.5707963267948966D:1.5707963267948966D));
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Orthographic Azimuthal";
    }
}
