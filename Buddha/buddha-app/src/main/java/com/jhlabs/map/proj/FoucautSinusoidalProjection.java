package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class FoucautSinusoidalProjection extends Projection {
    private double n;
    private double n1;
    private static final int MAX_ITER = 10;
    private static final double LOOP_TOL = 1.0E-7D;

    public FoucautSinusoidalProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double t = Math.cos(lpphi);
        out.x = lplam * t / (this.n + this.n1 * t);
        out.y = this.n * lpphi + this.n1 * Math.sin(lpphi);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double V;
        if(this.n != 0.0D) {
            out.y = xyy;

            int i;
            for(i = 10; i > 0; --i) {
                out.y -= V = (this.n * out.y + this.n1 * Math.sin(out.y) - xyy) / (this.n + this.n1 * Math.cos(out.y));
                if(Math.abs(V) < 1.0E-7D) {
                    break;
                }
            }

            if(i == 0) {
                out.y = xyy < 0.0D?-1.5707963267948966D:1.5707963267948966D;
            }
        } else {
            out.y = MapMath.asin(xyy);
        }

        V = Math.cos(out.y);
        out.x = xyx * (this.n + this.n1 * V) / V;
        return out;
    }

    public void initialize() {
        super.initialize();
        if(this.n >= 0.0D && this.n <= 1.0D) {
            this.n1 = 1.0D - this.n;
        } else {
            throw new ProjectionException("-99");
        }
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Foucaut Sinusoidal";
    }
}
