package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class CylindricalEqualAreaProjection extends Projection {
    private double qp;
    private double[] apa;
    private double trueScaleLatitude;

    public CylindricalEqualAreaProjection() {
        this(0.0D, 0.0D, 0.0D);
    }

    public CylindricalEqualAreaProjection(double projectionLatitude, double projectionLongitude, double trueScaleLatitude) {
        this.projectionLatitude = projectionLatitude;
        this.projectionLongitude = projectionLongitude;
        this.trueScaleLatitude = trueScaleLatitude;
        this.initialize();
    }

    public void initialize() {
        super.initialize();
        double t = this.trueScaleLatitude;
        this.scaleFactor = Math.cos(t);
        if(this.es != 0.0D) {
            t = Math.sin(t);
            this.scaleFactor /= Math.sqrt(1.0D - this.es * t * t);
            this.apa = MapMath.authset(this.es);
            this.qp = MapMath.qsfn(1.0D, this.e, this.one_es);
        }

    }

    public Double project(double lam, double phi, Double xy) {
        if(this.spherical) {
            xy.x = this.scaleFactor * lam;
            xy.y = Math.sin(phi) / this.scaleFactor;
        } else {
            xy.x = this.scaleFactor * lam;
            xy.y = 0.5D * MapMath.qsfn(Math.sin(phi), this.e, this.one_es) / this.scaleFactor;
        }

        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        if(this.spherical) {
            double t;
            if((t = Math.abs(y *= this.scaleFactor)) - 1.0E-10D > 1.0D) {
                throw new ProjectionException();
            }

            if(t >= 1.0D) {
                lp.y = y < 0.0D?-1.5707963267948966D:1.5707963267948966D;
            } else {
                lp.y = Math.asin(y);
            }

            lp.x = x / this.scaleFactor;
        } else {
            lp.y = MapMath.authlat(Math.asin(2.0D * y * this.scaleFactor / this.qp), this.apa);
            lp.x = x / this.scaleFactor;
        }

        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isRectilinear() {
        return true;
    }
}
