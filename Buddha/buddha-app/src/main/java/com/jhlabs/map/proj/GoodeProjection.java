package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.MolleweideProjection;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.SinusoidalProjection;

public class GoodeProjection extends Projection {
    private static final double Y_COR = 0.0528D;
    private static final double PHI_LIM = 0.7109307819790236D;
    private SinusoidalProjection sinu = new SinusoidalProjection();
    private MolleweideProjection moll = new MolleweideProjection();

    public GoodeProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        if(Math.abs(lpphi) <= 0.7109307819790236D) {
            out = this.sinu.project(lplam, lpphi, out);
        } else {
            out = this.moll.project(lplam, lpphi, out);
            out.y -= lpphi >= 0.0D?0.0528D:-0.0528D;
        }

        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        if(Math.abs(xyy) <= 0.7109307819790236D) {
            out = this.sinu.projectInverse(xyx, xyy, out);
        } else {
            xyy += xyy >= 0.0D?0.0528D:-0.0528D;
            out = this.moll.projectInverse(xyx, xyy, out);
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Goode Homolosine";
    }
}
