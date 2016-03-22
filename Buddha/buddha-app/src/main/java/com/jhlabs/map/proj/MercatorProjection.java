package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.CylindricalProjection;

public class MercatorProjection extends CylindricalProjection {
    public MercatorProjection() {
        this.minLatitude = MapMath.degToRad(-85.0D);
        this.maxLatitude = MapMath.degToRad(85.0D);
    }

    public Double project(double lam, double phi, Double out) {
        if(this.spherical) {
            out.x = this.scaleFactor * lam;
            out.y = this.scaleFactor * Math.log(Math.tan(0.7853981633974483D + 0.5D * phi));
        } else {
            out.x = this.scaleFactor * lam;
            out.y = -this.scaleFactor * Math.log(MapMath.tsfn(phi, Math.sin(phi), this.e));
        }

        return out;
    }

    public Double projectInverse(double x, double y, Double out) {
        if(this.spherical) {
            out.y = 1.5707963267948966D - 2.0D * Math.atan(Math.exp(-y / this.scaleFactor));
            out.x = x / this.scaleFactor;
        } else {
            out.y = MapMath.phi2(Math.exp(-y / this.scaleFactor), this.e);
            out.x = x / this.scaleFactor;
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isRectilinear() {
        return true;
    }

    public int getEPSGCode() {
        return 9804;
    }

    public String toString() {
        return "Mercator";
    }
}
