package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class URMFPSProjection extends Projection {
    private static final double C_x = 0.8773826753D;
    private static final double Cy = 1.139753528477D;
    private double n = 0.8660254037844386D;
    private double C_y;

    public URMFPSProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        out.y = MapMath.asin(this.n * Math.sin(lpphi));
        out.x = 0.8773826753D * lplam * Math.cos(lpphi);
        out.y = this.C_y * lpphi;
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        xyy /= this.C_y;
        out.y = MapMath.asin(Math.sin(xyy) / this.n);
        out.x = xyx / (0.8773826753D * Math.cos(xyy));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();
        if(this.n > 0.0D && this.n <= 1.0D) {
            this.C_y = 1.139753528477D / this.n;
        } else {
            throw new ProjectionException("-40");
        }
    }

    public void setN(double n) {
        this.n = n;
    }

    public double getN() {
        return this.n;
    }

    public String toString() {
        return "Urmaev Flat-Polar Sinusoidal";
    }
}
