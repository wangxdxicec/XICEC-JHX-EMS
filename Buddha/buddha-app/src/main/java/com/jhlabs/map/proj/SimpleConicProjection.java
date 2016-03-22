package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.ConicProjection;
import com.jhlabs.map.proj.ProjectionException;

public class SimpleConicProjection extends ConicProjection {
    private double n;
    private double rho_c;
    private double rho_0;
    private double sig;
    private double c1;
    private double c2;
    private int type;
    public static final int EULER = 0;
    public static final int MURD1 = 1;
    public static final int MURD2 = 2;
    public static final int MURD3 = 3;
    public static final int PCONIC = 4;
    public static final int TISSOT = 5;
    public static final int VITK1 = 6;
    private static final double EPS10 = 1.0E-10D;
    private static final double EPS = 1.0E-10D;

    public SimpleConicProjection() {
        this(0);
    }

    public SimpleConicProjection(int type) {
        this.type = type;
        this.minLatitude = Math.toRadians(0.0D);
        this.maxLatitude = Math.toRadians(80.0D);
    }

    public String toString() {
        return "Simple Conic";
    }

    public Double project(double lplam, double lpphi, Double out) {
        double rho;
        switch(this.type) {
            case 2:
                rho = this.rho_c + Math.tan(this.sig - lpphi);
                break;
            case 3:
            default:
                rho = this.rho_c - lpphi;
                break;
            case 4:
                rho = this.c2 * (this.c1 - Math.tan(lpphi));
        }

        out.x = rho * Math.sin(lplam *= this.n);
        out.y = this.rho_0 - rho * Math.cos(lplam);
        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double rho = MapMath.distance(xyx, out.y = this.rho_0 - xyy);
        if(this.n < 0.0D) {
            rho = -rho;
            out.x = -xyx;
            out.y = -xyy;
        }

        out.x = Math.atan2(xyx, xyy) / this.n;
        switch(this.type) {
            case 2:
                out.y = this.sig - Math.atan(rho - this.rho_c);
                break;
            case 3:
            default:
                out.y = this.rho_c - rho;
                break;
            case 4:
                out.y = Math.atan(this.c1 - rho / this.c2) + this.sig;
        }

        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();
        boolean err = false;
        double p1 = Math.toRadians(30.0D);
        double p2 = Math.toRadians(60.0D);
        double del = 0.5D * (p2 - p1);
        this.sig = 0.5D * (p2 + p1);
        int err1 = Math.abs(del) >= 1.0E-10D && Math.abs(this.sig) >= 1.0E-10D?0:-42;
        if(err1 != 0) {
            throw new ProjectionException("Error " + err1);
        } else {
            double cs;
            switch(this.type) {
                case 0:
                    this.n = Math.sin(this.sig) * Math.sin(del) / del;
                    del *= 0.5D;
                    this.rho_c = del / (Math.tan(del) * Math.tan(this.sig)) + this.sig;
                    this.rho_0 = this.rho_c - this.projectionLatitude;
                    break;
                case 1:
                    this.rho_c = Math.sin(del) / (del * Math.tan(this.sig)) + this.sig;
                    this.rho_0 = this.rho_c - this.projectionLatitude;
                    this.n = Math.sin(this.sig);
                    break;
                case 2:
                    this.rho_c = (cs = Math.sqrt(Math.cos(del))) / Math.tan(this.sig);
                    this.rho_0 = this.rho_c + Math.tan(this.sig - this.projectionLatitude);
                    this.n = Math.sin(this.sig) * cs;
                    break;
                case 3:
                    this.rho_c = del / (Math.tan(this.sig) * Math.tan(del)) + this.sig;
                    this.rho_0 = this.rho_c - this.projectionLatitude;
                    this.n = Math.sin(this.sig) * Math.sin(del) * Math.tan(del) / (del * del);
                    break;
                case 4:
                    this.n = Math.sin(this.sig);
                    this.c2 = Math.cos(del);
                    this.c1 = 1.0D / Math.tan(this.sig);
                    if(Math.abs(del = this.projectionLatitude - this.sig) - 1.0E-10D >= 1.5707963267948966D) {
                        throw new ProjectionException("-43");
                    }

                    this.rho_0 = this.c2 * (this.c1 - Math.tan(del));
                    this.maxLatitude = Math.toRadians(60.0D);
                    break;
                case 5:
                    this.n = Math.sin(this.sig);
                    cs = Math.cos(del);
                    this.rho_c = this.n / cs + cs / this.n;
                    this.rho_0 = Math.sqrt((this.rho_c - 2.0D * Math.sin(this.projectionLatitude)) / this.n);
                    break;
                case 6:
                    this.n = (cs = Math.tan(del)) * Math.sin(this.sig) / del;
                    this.rho_c = del / (cs * Math.tan(this.sig)) + this.sig;
                    this.rho_0 = this.rho_c - this.projectionLatitude;
            }

        }
    }
}
