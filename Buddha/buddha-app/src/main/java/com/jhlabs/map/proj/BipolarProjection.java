package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class BipolarProjection extends Projection {
    private boolean noskew;
    private static final double EPS = 1.0E-10D;
    private static final double EPS10 = 1.0E-10D;
    private static final double ONEEPS = 1.000000001D;
    private static final int NITER = 10;
    private static final double lamB = -0.3489497672625068D;
    private static final double n = 0.6305584488127469D;
    private static final double F = 1.8972474256746104D;
    private static final double Azab = 0.8165004367468637D;
    private static final double Azba = 1.8226184385618593D;
    private static final double T = 1.27246578267089D;
    private static final double rhoc = 1.2070912152156872D;
    private static final double cAzc = 0.6969152303867837D;
    private static final double sAzc = 0.7171535133114361D;
    private static final double C45 = 0.7071067811865476D;
    private static final double S45 = 0.7071067811865476D;
    private static final double C20 = 0.9396926207859084D;
    private static final double S20 = -0.3420201433256687D;
    private static final double R110 = 1.9198621771937625D;
    private static final double R104 = 1.8151424220741028D;

    public BipolarProjection() {
        this.minLatitude = Math.toRadians(-80.0D);
        this.maxLatitude = Math.toRadians(80.0D);
        this.projectionLongitude = Math.toRadians(-90.0D);
        this.minLongitude = Math.toRadians(-90.0D);
        this.maxLongitude = Math.toRadians(90.0D);
    }

    public Double project(double lplam, double lpphi, Double out) {
        double cphi = Math.cos(lpphi);
        double sphi = Math.sin(lpphi);
        double sdlam;
        double cdlam = Math.cos(sdlam = -0.3489497672625068D - lplam);
        sdlam = Math.sin(sdlam);
        double tphi;
        double Az;
        if(Math.abs(Math.abs(lpphi) - 1.5707963267948966D) < 1.0E-10D) {
            Az = lpphi < 0.0D?3.141592653589793D:0.0D;
            tphi = 1.7976931348623157E308D;
        } else {
            tphi = sphi / cphi;
            Az = Math.atan2(sdlam, 0.7071067811865476D * (tphi - cdlam));
        }

        double z;
        double Av;
        boolean tag;
        if(tag = Az > 1.8226184385618593D) {
            cdlam = Math.cos(sdlam = lplam + 1.9198621771937625D);
            sdlam = Math.sin(sdlam);
            z = -0.3420201433256687D * sphi + 0.9396926207859084D * cphi * cdlam;
            if(Math.abs(z) > 1.0D) {
                if(Math.abs(z) > 1.000000001D) {
                    throw new ProjectionException("F");
                }

                z = z < 0.0D?-1.0D:1.0D;
            } else {
                z = Math.acos(z);
            }

            if(tphi != 1.7976931348623157E308D) {
                Az = Math.atan2(sdlam, 0.9396926207859084D * tphi - -0.3420201433256687D * cdlam);
            }

            Av = 0.8165004367468637D;
            out.y = 1.2070912152156872D;
        } else {
            z = 0.7071067811865476D * (sphi + cphi * cdlam);
            if(Math.abs(z) > 1.0D) {
                if(Math.abs(z) > 1.000000001D) {
                    throw new ProjectionException("F");
                }

                z = z < 0.0D?-1.0D:1.0D;
            } else {
                z = Math.acos(z);
            }

            Av = 1.8226184385618593D;
            out.y = -1.2070912152156872D;
        }

        if(z < 0.0D) {
            throw new ProjectionException("F");
        } else {
            double t;
            double r = 1.8972474256746104D * (t = Math.pow(Math.tan(0.5D * z), 0.6305584488127469D));
            double al;
            if((al = 0.5D * (1.8151424220741028D - z)) < 0.0D) {
                throw new ProjectionException("F");
            } else {
                al = (t + Math.pow(al, 0.6305584488127469D)) / 1.27246578267089D;
                if(Math.abs(al) > 1.0D) {
                    if(Math.abs(al) > 1.000000001D) {
                        throw new ProjectionException("F");
                    }

                    al = al < 0.0D?-1.0D:1.0D;
                } else {
                    al = Math.acos(al);
                }

                if(Math.abs(t = 0.6305584488127469D * (Av - Az)) < al) {
                    r /= Math.cos(al + (tag?t:-t));
                }

                out.x = r * Math.sin(t);
                out.y += (tag?-r:r) * Math.cos(t);
                if(this.noskew) {
                    t = out.x;
                    out.x = -out.x * 0.6969152303867837D - out.y * 0.7171535133114361D;
                    out.y = -out.y * 0.6969152303867837D + t * 0.7171535133114361D;
                }

                return out;
            }
        }
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double z = 0.0D;
        if(this.noskew) {
            out.x = -xyx * 0.6969152303867837D + xyy * 0.7171535133114361D;
            out.y = -xyy * 0.6969152303867837D - xyx * 0.7171535133114361D;
        }

        double s;
        double c;
        double Av;
        boolean neg;
        if(neg = xyx < 0.0D) {
            out.y = 1.2070912152156872D - xyy;
            s = -0.3420201433256687D;
            c = 0.9396926207859084D;
            Av = 0.8165004367468637D;
        } else {
            ++out.y;
            s = 0.7071067811865476D;
            c = 0.7071067811865476D;
            Av = 1.8226184385618593D;
        }

        double r;
        double rp;
        double rl = rp = r = MapMath.distance(xyx, xyy);
        double Az;
        double fAz = Math.abs(Az = Math.atan2(xyx, xyy));

        int i;
        for(i = 10; i > 0; --i) {
            z = 2.0D * Math.atan(Math.pow(r / 1.8972474256746104D, 1.585895806935677D));
            double al = Math.acos((Math.pow(Math.tan(0.5D * z), 0.6305584488127469D) + Math.pow(Math.tan(0.5D * (1.8151424220741028D - z)), 0.6305584488127469D)) / 1.27246578267089D);
            if(fAz < al) {
                r = rp * Math.cos(al + (neg?Az:-Az));
            }

            if(Math.abs(rl - r) < 1.0E-10D) {
                break;
            }

            rl = r;
        }

        if(i == 0) {
            throw new ProjectionException("I");
        } else {
            Az = Av - Az / 0.6305584488127469D;
            out.y = Math.asin(s * Math.cos(z) + c * Math.sin(z) * Math.cos(Az));
            out.x = Math.atan2(Math.sin(Az), c / Math.tan(z) - s * Math.cos(Az));
            if(neg) {
                --out.x;
            } else {
                out.x = -0.3489497672625068D - out.x;
            }

            return out;
        }
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();
    }

    public String toString() {
        return "Bipolar Conic of Western Hemisphere";
    }
}
