package com.jhlabs.map;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Rectangle2D;
import com.jhlabs.map.Rectangle2D.Double;
import com.jhlabs.map.proj.ProjectionException;

public class MapMath {
    public static final double HALFPI = 1.5707963267948966D;
    public static final double QUARTERPI = 0.7853981633974483D;
    public static final double TWOPI = 6.283185307179586D;
    public static final double RTD = 57.29577951308232D;
    public static final double DTR = 0.017453292519943295D;
    public static final Rectangle2D WORLD_BOUNDS_RAD = new Double(-3.141592653589793D, -1.5707963267948966D, 6.283185307179586D, 3.141592653589793D);
    public static final Rectangle2D WORLD_BOUNDS = new Double(-180.0D, -90.0D, 360.0D, 180.0D);
    public static final int DONT_INTERSECT = 0;
    public static final int DO_INTERSECT = 1;
    public static final int COLLINEAR = 2;
    private static final int N_ITER = 15;
    private static final double C00 = 1.0D;
    private static final double C02 = 0.25D;
    private static final double C04 = 0.046875D;
    private static final double C06 = 0.01953125D;
    private static final double C08 = 0.01068115234375D;
    private static final double C22 = 0.75D;
    private static final double C44 = 0.46875D;
    private static final double C46 = 0.013020833333333334D;
    private static final double C48 = 0.007120768229166667D;
    private static final double C66 = 0.3645833333333333D;
    private static final double C68 = 0.005696614583333333D;
    private static final double C88 = 0.3076171875D;
    private static final int MAX_ITER = 10;
    private static final double P00 = 0.3333333333333333D;
    private static final double P01 = 0.17222222222222222D;
    private static final double P02 = 0.10257936507936508D;
    private static final double P10 = 0.06388888888888888D;
    private static final double P11 = 0.0664021164021164D;
    private static final double P20 = 0.016415012942191543D;

    public MapMath() {
    }

    public static double sind(double v) {
        return Math.sin(v * 0.017453292519943295D);
    }

    public static double cosd(double v) {
        return Math.cos(v * 0.017453292519943295D);
    }

    public static double tand(double v) {
        return Math.tan(v * 0.017453292519943295D);
    }

    public static double asind(double v) {
        return Math.asin(v) * 57.29577951308232D;
    }

    public static double acosd(double v) {
        return Math.acos(v) * 57.29577951308232D;
    }

    public static double atand(double v) {
        return Math.atan(v) * 57.29577951308232D;
    }

    public static double atan2d(double y, double x) {
        return Math.atan2(y, x) * 57.29577951308232D;
    }

    public static double asin(double v) {
        return Math.abs(v) > 1.0D?(v < 0.0D?-1.5707963267948966D:1.5707963267948966D):Math.asin(v);
    }

    public static double acos(double v) {
        return Math.abs(v) > 1.0D?(v < 0.0D?3.141592653589793D:0.0D):Math.acos(v);
    }

    public static double sqrt(double v) {
        return v < 0.0D?0.0D:Math.sqrt(v);
    }

    public static double distance(double dx, double dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(com.jhlabs.map.Point2D.Double a, com.jhlabs.map.Point2D.Double b) {
        return distance(a.x - b.x, a.y - b.y);
    }

    public static double hypot(double x, double y) {
        if(x < 0.0D) {
            x = -x;
        } else if(x == 0.0D) {
            return y < 0.0D?-y:y;
        }

        if(y < 0.0D) {
            y = -y;
        } else if(y == 0.0D) {
            return x;
        }

        if(x < y) {
            x /= y;
            return y * Math.sqrt(1.0D + x * x);
        } else {
            y /= x;
            return x * Math.sqrt(1.0D + y * y);
        }
    }

    public static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    public static double trunc(double v) {
        return v < 0.0D?Math.ceil(v):Math.floor(v);
    }

    public static double frac(double v) {
        return v - trunc(v);
    }

    public static double degToRad(double v) {
        return v * 3.141592653589793D / 180.0D;
    }

    public static double radToDeg(double v) {
        return v * 180.0D / 3.141592653589793D;
    }

    public static double dmsToRad(double d, double m, double s) {
        return d >= 0.0D?(d + m / 60.0D + s / 3600.0D) * 3.141592653589793D / 180.0D:(d - m / 60.0D - s / 3600.0D) * 3.141592653589793D / 180.0D;
    }

    public static double dmsToDeg(double d, double m, double s) {
        return d >= 0.0D?d + m / 60.0D + s / 3600.0D:d - m / 60.0D - s / 3600.0D;
    }

    public static double normalizeLatitude(double angle) {
        if(!java.lang.Double.isInfinite(angle) && !java.lang.Double.isNaN(angle)) {
            while(angle > 1.5707963267948966D) {
                angle -= 3.141592653589793D;
            }

            while(angle < -1.5707963267948966D) {
                angle += 3.141592653589793D;
            }

            return angle;
        } else {
            throw new ProjectionException("Infinite latitude");
        }
    }

    public static double normalizeLongitude(double angle) {
        if(!java.lang.Double.isInfinite(angle) && !java.lang.Double.isNaN(angle)) {
            while(angle > 3.141592653589793D) {
                angle -= 6.283185307179586D;
            }

            while(angle < -3.141592653589793D) {
                angle += 6.283185307179586D;
            }

            return angle;
        } else {
            throw new ProjectionException("Infinite longitude");
        }
    }

    public static double normalizeAngle(double angle) {
        if(!java.lang.Double.isInfinite(angle) && !java.lang.Double.isNaN(angle)) {
            while(angle > 6.283185307179586D) {
                angle -= 6.283185307179586D;
            }

            while(angle < 0.0D) {
                angle += 6.283185307179586D;
            }

            return angle;
        } else {
            throw new ProjectionException("Infinite angle");
        }
    }

    public static double greatCircleDistance(double lon1, double lat1, double lon2, double lat2) {
        double dlat = Math.sin((lat2 - lat1) / 2.0D);
        double dlon = Math.sin((lon2 - lon1) / 2.0D);
        double r = Math.sqrt(dlat * dlat + Math.cos(lat1) * Math.cos(lat2) * dlon * dlon);
        return 2.0D * Math.asin(r);
    }

    public static double sphericalAzimuth(double lat0, double lon0, double lat, double lon) {
        double diff = lon - lon0;
        double coslat = Math.cos(lat);
        return Math.atan2(coslat * Math.sin(diff), Math.cos(lat0) * Math.sin(lat) - Math.sin(lat0) * coslat * Math.cos(diff));
    }

    public static boolean sameSigns(double a, double b) {
        return a < 0.0D == b < 0.0D;
    }

    public static boolean sameSigns(int a, int b) {
        return a < 0 == b < 0;
    }

    public static double takeSign(double a, double b) {
        a = Math.abs(a);
        return b < 0.0D?-a:a;
    }

    public static int takeSign(int a, int b) {
        a = Math.abs(a);
        return b < 0?-a:a;
    }

    public static int intersectSegments(com.jhlabs.map.Point2D.Double aStart, com.jhlabs.map.Point2D.Double aEnd, com.jhlabs.map.Point2D.Double bStart, com.jhlabs.map.Point2D.Double bEnd, com.jhlabs.map.Point2D.Double p) {
        double a1 = aEnd.y - aStart.y;
        double b1 = aStart.x - aEnd.x;
        double c1 = aEnd.x * aStart.y - aStart.x * aEnd.y;
        double r3 = a1 * bStart.x + b1 * bStart.y + c1;
        double r4 = a1 * bEnd.x + b1 * bEnd.y + c1;
        if(r3 != 0.0D && r4 != 0.0D && sameSigns(r3, r4)) {
            return 0;
        } else {
            double a2 = bEnd.y - bStart.y;
            double b2 = bStart.x - bEnd.x;
            double c2 = bEnd.x * bStart.y - bStart.x * bEnd.y;
            double r1 = a2 * aStart.x + b2 * aStart.y + c2;
            double r2 = a2 * aEnd.x + b2 * aEnd.y + c2;
            if(r1 != 0.0D && r2 != 0.0D && sameSigns(r1, r2)) {
                return 0;
            } else {
                double denom = a1 * b2 - a2 * b1;
                if(denom == 0.0D) {
                    return 2;
                } else {
                    double offset = denom < 0.0D?-denom / 2.0D:denom / 2.0D;
                    double num = b1 * c2 - b2 * c1;
                    p.x = (num < 0.0D?num - offset:num + offset) / denom;
                    num = a2 * c1 - a1 * c2;
                    p.y = (num < 0.0D?num - offset:num + offset) / denom;
                    return 1;
                }
            }
        }
    }

    public static double dot(com.jhlabs.map.Point2D.Double a, com.jhlabs.map.Point2D.Double b) {
        return a.x * b.x + a.y * b.y;
    }

    public static com.jhlabs.map.Point2D.Double perpendicular(com.jhlabs.map.Point2D.Double a) {
        return new com.jhlabs.map.Point2D.Double(-a.y, a.x);
    }

    public static com.jhlabs.map.Point2D.Double add(com.jhlabs.map.Point2D.Double a, com.jhlabs.map.Point2D.Double b) {
        return new com.jhlabs.map.Point2D.Double(a.x + b.x, a.y + b.y);
    }

    public static com.jhlabs.map.Point2D.Double subtract(com.jhlabs.map.Point2D.Double a, com.jhlabs.map.Point2D.Double b) {
        return new com.jhlabs.map.Point2D.Double(a.x - b.x, a.y - b.y);
    }

    public static com.jhlabs.map.Point2D.Double multiply(com.jhlabs.map.Point2D.Double a, com.jhlabs.map.Point2D.Double b) {
        return new com.jhlabs.map.Point2D.Double(a.x * b.x, a.y * b.y);
    }

    public static double cross(com.jhlabs.map.Point2D.Double a, com.jhlabs.map.Point2D.Double b) {
        return a.x * b.y - b.x * a.y;
    }

    public static double cross(double x1, double y1, double x2, double y2) {
        return x1 * y2 - x2 * y1;
    }

    public static void normalize(com.jhlabs.map.Point2D.Double a) {
        double d = distance(a.x, a.y);
        a.x /= d;
        a.y /= d;
    }

    public static void negate(com.jhlabs.map.Point2D.Double a) {
        a.x = -a.x;
        a.y = -a.y;
    }

    public static double longitudeDistance(double l1, double l2) {
        return Math.min(Math.abs(l1 - l2), (l1 < 0.0D?l1 + 3.141592653589793D:3.141592653589793D - l1) + (l2 < 0.0D?l2 + 3.141592653589793D:3.141592653589793D - l2));
    }

    public static double geocentricLatitude(double lat, double flatness) {
        double f = 1.0D - flatness;
        return Math.atan(f * f * Math.tan(lat));
    }

    public static double geographicLatitude(double lat, double flatness) {
        double f = 1.0D - flatness;
        return Math.atan(Math.tan(lat) / (f * f));
    }

    public static double tsfn(double phi, double sinphi, double e) {
        sinphi *= e;
        return Math.tan(0.5D * (1.5707963267948966D - phi)) / Math.pow((1.0D - sinphi) / (1.0D + sinphi), 0.5D * e);
    }

    public static double msfn(double sinphi, double cosphi, double es) {
        return cosphi / Math.sqrt(1.0D - es * sinphi * sinphi);
    }

    public static double phi2(double ts, double e) {
        double eccnth = 0.5D * e;
        double phi = 1.5707963267948966D - 2.0D * Math.atan(ts);
        int i = 15;

        do {
            double con = e * Math.sin(phi);
            double dphi = 1.5707963267948966D - 2.0D * Math.atan(ts * Math.pow((1.0D - con) / (1.0D + con), eccnth)) - phi;
            phi += dphi;
            if(Math.abs(dphi) <= 1.0E-10D) {
                break;
            }

            --i;
        } while(i != 0);

        if(i <= 0) {
            throw new ProjectionException();
        } else {
            return phi;
        }
    }

    public static double[] enfn(double es) {
        double t;
        double[] en = new double[]{1.0D - es * (0.25D + es * (0.046875D + es * (0.01953125D + es * 0.01068115234375D))), es * (0.75D - es * (0.046875D + es * (0.01953125D + es * 0.01068115234375D))), (t = es * es) * (0.46875D - es * (0.013020833333333334D + es * 0.007120768229166667D)), (t *= es) * (0.3645833333333333D - es * 0.005696614583333333D), t * es * 0.3076171875D};
        return en;
    }

    public static double mlfn(double phi, double sphi, double cphi, double[] en) {
        cphi *= sphi;
        sphi *= sphi;
        return en[0] * phi - cphi * (en[1] + sphi * (en[2] + sphi * (en[3] + sphi * en[4])));
    }

    public static double inv_mlfn(double arg, double es, double[] en) {
        double k = 1.0D / (1.0D - es);
        double phi = arg;

        for(int i = 10; i != 0; --i) {
            double s = Math.sin(phi);
            double t = 1.0D - es * s * s;
            phi -= t = (mlfn(phi, s, Math.cos(phi), en) - arg) * t * Math.sqrt(t) * k;
            if(Math.abs(t) < 1.0E-11D) {
                return phi;
            }
        }

        return phi;
    }

    public static double[] authset(double es) {
        double[] APA = new double[]{es * 0.3333333333333333D, 0.0D, 0.0D};
        double t = es * es;
        APA[0] += t * 0.17222222222222222D;
        APA[1] = t * 0.06388888888888888D;
        t *= es;
        APA[0] += t * 0.10257936507936508D;
        APA[1] += t * 0.0664021164021164D;
        APA[2] = t * 0.016415012942191543D;
        return APA;
    }

    public static double authlat(double beta, double[] APA) {
        double t = beta + beta;
        return beta + APA[0] * Math.sin(t) + APA[1] * Math.sin(t + t) + APA[2] * Math.sin(t + t + t);
    }

    public static double qsfn(double sinphi, double e, double one_es) {
        if(e >= 1.0E-7D) {
            double con = e * sinphi;
            return one_es * (sinphi / (1.0D - con * con) - 0.5D / e * Math.log((1.0D - con) / (1.0D + con)));
        } else {
            return sinphi + sinphi;
        }
    }

    public static double niceNumber(double x, boolean round) {
        int expv = (int)Math.floor(Math.log(x) / Math.log(10.0D));
        double f = x / Math.pow(10.0D, (double)expv);
        double nf;
        if(round) {
            if(f < 1.5D) {
                nf = 1.0D;
            } else if(f < 3.0D) {
                nf = 2.0D;
            } else if(f < 7.0D) {
                nf = 5.0D;
            } else {
                nf = 10.0D;
            }
        } else if(f <= 1.0D) {
            nf = 1.0D;
        } else if(f <= 2.0D) {
            nf = 2.0D;
        } else if(f <= 5.0D) {
            nf = 5.0D;
        } else {
            nf = 10.0D;
        }

        return nf * Math.pow(10.0D, (double)expv);
    }
}
