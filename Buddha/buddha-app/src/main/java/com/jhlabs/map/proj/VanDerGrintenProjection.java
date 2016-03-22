package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class VanDerGrintenProjection extends Projection {
    private static final double TOL = 1.0E-10D;
    private static final double THIRD = 0.3333333333333333D;
    private static final double TWO_THRD = 0.6666666666666666D;
    private static final double C2_27 = 0.07407407407407407D;
    private static final double PI4_3 = 4.188790204786391D;
    private static final double PISQ = 9.869604401089358D;
    private static final double TPISQ = 19.739208802178716D;
    private static final double HPISQ = 4.934802200544679D;

    public VanDerGrintenProjection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double p2 = Math.abs(lpphi / 1.5707963267948966D);
        if(p2 - 1.0E-10D > 1.0D) {
            throw new ProjectionException("F");
        } else {
            if(p2 > 1.0D) {
                p2 = 1.0D;
            }

            if(Math.abs(lpphi) <= 1.0E-10D) {
                out.x = lplam;
                out.y = 0.0D;
            } else if(Math.abs(lplam) > 1.0E-10D && Math.abs(p2 - 1.0D) >= 1.0E-10D) {
                double al = 0.5D * Math.abs(3.141592653589793D / lplam - lplam / 3.141592653589793D);
                double al2 = al * al;
                double g = Math.sqrt(1.0D - p2 * p2);
                g /= p2 + g - 1.0D;
                double g2 = g * g;
                p2 = g * (2.0D / p2 - 1.0D);
                p2 *= p2;
                out.x = g - p2;
                g = p2 + al2;
                out.x = 3.141592653589793D * (al * out.x + Math.sqrt(al2 * out.x * out.x - g * (g2 - p2))) / g;
                if(lplam < 0.0D) {
                    out.x = -out.x;
                }

                out.y = Math.abs(out.x / 3.141592653589793D);
                out.y = 1.0D - out.y * (out.y + 2.0D * al);
                if(out.y < -1.0E-10D) {
                    throw new ProjectionException("F");
                }

                if(out.y < 0.0D) {
                    out.y = 0.0D;
                } else {
                    out.y = Math.sqrt(out.y) * (lpphi < 0.0D?-3.141592653589793D:3.141592653589793D);
                }
            } else {
                out.x = 0.0D;
                out.y = 3.141592653589793D * Math.tan(0.5D * Math.asin(p2));
                if(lpphi < 0.0D) {
                    out.y = -out.y;
                }
            }

            return out;
        }
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double x2 = xyx * xyx;
        double t;
        double ay;
        if((ay = Math.abs(xyy)) < 1.0E-10D) {
            out.y = 0.0D;
            t = x2 * x2 + 19.739208802178716D * (x2 + 4.934802200544679D);
            out.x = Math.abs(xyx) <= 1.0E-10D?0.0D:0.5D * (x2 - 9.869604401089358D + Math.sqrt(t)) / xyx;
            return out;
        } else {
            double y2 = xyy * xyy;
            double r = x2 + y2;
            double r2 = r * r;
            double c1 = -3.141592653589793D * ay * (r + 9.869604401089358D);
            double c3 = r2 + 6.283185307179586D * (ay * r + 3.141592653589793D * (y2 + 3.141592653589793D * (ay + 1.5707963267948966D)));
            double c2 = c1 + 9.869604401089358D * (r - 3.0D * y2);
            double c0 = 3.141592653589793D * ay;
            c2 /= c3;
            double al = c1 / c3 - 0.3333333333333333D * c2 * c2;
            double m = 2.0D * Math.sqrt(-0.3333333333333333D * al);
            double d = 0.07407407407407407D * c2 * c2 * c2 + (c0 * c0 - 0.3333333333333333D * c2 * c1) / c3;
            if((t = Math.abs(d = 3.0D * d / (al * m))) - 1.0E-10D <= 1.0D) {
                d = t > 1.0D?(d > 0.0D?0.0D:3.141592653589793D):Math.acos(d);
                out.y = 3.141592653589793D * (m * Math.cos(d * 0.3333333333333333D + 4.188790204786391D) - 0.3333333333333333D * c2);
                if(xyy < 0.0D) {
                    out.y = -out.y;
                }

                t = r2 + 19.739208802178716D * (x2 - y2 + 4.934802200544679D);
                out.x = Math.abs(xyx) <= 1.0E-10D?0.0D:0.5D * (r - 9.869604401089358D + (t <= 0.0D?0.0D:Math.sqrt(t))) / xyx;
                return out;
            } else {
                throw new ProjectionException("I");
            }
        }
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "van der Grinten (I)";
    }
}
