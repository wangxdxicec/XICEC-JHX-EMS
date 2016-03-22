package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.CylindricalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class TCCProjection extends CylindricalProjection {
    public TCCProjection() {
        this.minLongitude = MapMath.degToRad(-60.0D);
        this.maxLongitude = MapMath.degToRad(60.0D);
    }

    public Double project(double lplam, double lpphi, Double out) {
        double b = Math.cos(lpphi) * Math.sin(lplam);
        double bt;
        if((bt = 1.0D - b * b) < 1.0E-10D) {
            throw new ProjectionException("F");
        } else {
            out.x = b / Math.sqrt(bt);
            out.y = Math.atan2(Math.tan(lpphi), Math.cos(lplam));
            return out;
        }
    }

    public boolean isRectilinear() {
        return false;
    }

    public String toString() {
        return "Transverse Central Cylindrical";
    }
}
