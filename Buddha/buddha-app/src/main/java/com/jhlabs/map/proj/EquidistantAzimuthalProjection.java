package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.AzimuthalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class EquidistantAzimuthalProjection extends AzimuthalProjection {
    private static final double TOL = 1.0E-8D;
    private int mode;
    private double[] en;
    private double M1;
    private double N1;
    private double Mp;
    private double He;
    private double G;
    private double sinphi0;
    private double cosphi0;

    public EquidistantAzimuthalProjection() {
        this(Math.toRadians(90.0D), Math.toRadians(0.0D));
    }

    public EquidistantAzimuthalProjection(double projectionLatitude, double projectionLongitude) {
        super(projectionLatitude, projectionLongitude);
        this.initialize();
    }

    public Object clone() {
        EquidistantAzimuthalProjection p = (EquidistantAzimuthalProjection)super.clone();
        if(this.en != null) {
            p.en = (double[])this.en.clone();
        }

        return p;
    }

    public void initialize() {
        super.initialize();
        if(Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) < 1.0E-10D) {
            this.mode = this.projectionLatitude < 0.0D?2:1;
            this.sinphi0 = this.projectionLatitude < 0.0D?-1.0D:1.0D;
            this.cosphi0 = 0.0D;
        } else if(Math.abs(this.projectionLatitude) < 1.0E-10D) {
            this.mode = 3;
            this.sinphi0 = 0.0D;
            this.cosphi0 = 1.0D;
        } else {
            this.mode = 4;
            this.sinphi0 = Math.sin(this.projectionLatitude);
            this.cosphi0 = Math.cos(this.projectionLatitude);
        }

        if(!this.spherical) {
            this.en = MapMath.enfn(this.es);
            switch(this.mode) {
                case 1:
                    this.Mp = MapMath.mlfn(1.5707963267948966D, 1.0D, 0.0D, this.en);
                    break;
                case 2:
                    this.Mp = MapMath.mlfn(-1.5707963267948966D, -1.0D, 0.0D, this.en);
                    break;
                case 3:
                case 4:
                    this.N1 = 1.0D / Math.sqrt(1.0D - this.es * this.sinphi0 * this.sinphi0);
                    this.G = this.sinphi0 * (this.He = this.e / Math.sqrt(this.one_es));
                    this.He *= this.cosphi0;
            }
        }

    }

    public Double project(double lam, double phi, Double xy) {
        double coslam;
        double cosphi;
        double sinphi;
        if(this.spherical) {
            sinphi = Math.sin(phi);
            cosphi = Math.cos(phi);
            coslam = Math.cos(lam);
            switch(this.mode) {
                case 1:
                    phi = -phi;
                    coslam = -coslam;
                case 2:
                    if(Math.abs(phi - 1.5707963267948966D) < 1.0E-10D) {
                        throw new ProjectionException();
                    }

                    xy.x = (xy.y = 1.5707963267948966D + phi) * Math.sin(lam);
                    xy.y *= coslam;
                    break;
                case 3:
                case 4:
                    if(this.mode == 3) {
                        xy.y = cosphi * coslam;
                    } else {
                        xy.y = this.sinphi0 * sinphi + this.cosphi0 * cosphi * coslam;
                    }

                    if(Math.abs(Math.abs(xy.y) - 1.0D) < 1.0E-8D) {
                        if(xy.y < 0.0D) {
                            throw new ProjectionException();
                        }

                        xy.x = xy.y = 0.0D;
                    } else {
                        xy.y = Math.acos(xy.y);
                        xy.y /= Math.sin(xy.y);
                        xy.x = xy.y * cosphi * Math.sin(lam);
                        xy.y *= this.mode == 3?sinphi:this.cosphi0 * sinphi - this.sinphi0 * cosphi * coslam;
                    }
            }
        } else {
            coslam = Math.cos(lam);
            cosphi = Math.cos(phi);
            sinphi = Math.sin(phi);
            switch(this.mode) {
                case 1:
                    coslam = -coslam;
                case 2:
                    double rho;
                    xy.x = (rho = Math.abs(this.Mp - MapMath.mlfn(phi, sinphi, cosphi, this.en))) * Math.sin(lam);
                    xy.y = rho * coslam;
                    break;
                case 3:
                case 4:
                    if(Math.abs(lam) < 1.0E-10D && Math.abs(phi - this.projectionLatitude) < 1.0E-10D) {
                        xy.x = xy.y = 0.0D;
                    } else {
                        double t = Math.atan2(this.one_es * sinphi + this.es * this.N1 * this.sinphi0 * Math.sqrt(1.0D - this.es * sinphi * sinphi), cosphi);
                        double ct = Math.cos(t);
                        double st = Math.sin(t);
                        double Az = Math.atan2(Math.sin(lam) * ct, this.cosphi0 * st - this.sinphi0 * coslam * ct);
                        double cA = Math.cos(Az);
                        double sA = Math.sin(Az);
                        double s = MapMath.asin(Math.abs(sA) < 1.0E-8D?(this.cosphi0 * st - this.sinphi0 * coslam * ct) / cA:Math.sin(lam) * ct / sA);
                        double H = this.He * cA;
                        double H2 = H * H;
                        double c = this.N1 * s * (1.0D + s * s * (-H2 * (1.0D - H2) / 6.0D + s * (this.G * H * (1.0D - 2.0D * H2 * H2) / 8.0D + s * ((H2 * (4.0D - 7.0D * H2) - 3.0D * this.G * this.G * (1.0D - 7.0D * H2)) / 120.0D - s * this.G * H / 48.0D))));
                        xy.x = c * sA;
                        xy.y = c * cA;
                    }
            }
        }

        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        double c;
        double Az;
        double cosAz;
        if(this.spherical) {
            if((Az = MapMath.distance(x, y)) > 3.141592653589793D) {
                if(Az - 1.0E-10D > 3.141592653589793D) {
                    throw new ProjectionException();
                }

                Az = 3.141592653589793D;
            } else if(Az < 1.0E-10D) {
                lp.y = this.projectionLatitude;
                lp.x = 0.0D;
                return lp;
            }

            if(this.mode != 4 && this.mode != 3) {
                if(this.mode == 1) {
                    lp.y = 1.5707963267948966D - Az;
                    lp.x = Math.atan2(x, -y);
                } else {
                    lp.y = Az - 1.5707963267948966D;
                    lp.x = Math.atan2(x, y);
                }
            } else {
                cosAz = Math.sin(Az);
                c = Math.cos(Az);
                if(this.mode == 3) {
                    lp.y = MapMath.asin(y * cosAz / Az);
                    x *= cosAz;
                    y = c * Az;
                } else {
                    lp.y = MapMath.asin(c * this.sinphi0 + y * cosAz * this.cosphi0 / Az);
                    y = (c - this.sinphi0 * Math.sin(lp.y)) * Az;
                    x *= cosAz * this.cosphi0;
                }

                lp.x = y == 0.0D?0.0D:Math.atan2(x, y);
            }
        } else {
            if((c = MapMath.distance(x, y)) < 1.0E-10D) {
                lp.y = this.projectionLatitude;
                lp.x = 0.0D;
                return lp;
            }

            if(this.mode != 4 && this.mode != 3) {
                lp.y = MapMath.inv_mlfn(this.mode == 1?this.Mp - c:this.Mp + c, this.es, this.en);
                lp.x = Math.atan2(x, this.mode == 1?-y:y);
            } else {
                cosAz = Math.cos(Az = Math.atan2(x, y));
                double t = this.cosphi0 * cosAz;
                double B = this.es * t / this.one_es;
                double A = -B * t;
                B *= 3.0D * (1.0D - A) * this.sinphi0;
                double D = c / this.N1;
                double E = D * (1.0D - D * D * (A * (1.0D + A) / 6.0D + B * (1.0D + 3.0D * A) * D / 24.0D));
                double F = 1.0D - E * E * (A / 2.0D + B * E / 6.0D);
                double psi = MapMath.asin(this.sinphi0 * Math.cos(E) + t * Math.sin(E));
                lp.x = MapMath.asin(Math.sin(Az) * Math.sin(E) / Math.cos(psi));
                if((t = Math.abs(psi)) < 1.0E-10D) {
                    lp.y = 0.0D;
                } else if(Math.abs(t - 1.5707963267948966D) < 0.0D) {
                    lp.y = 1.5707963267948966D;
                } else {
                    lp.y = Math.atan((1.0D - this.es * F * this.sinphi0 / Math.sin(psi)) * Math.tan(psi) / this.one_es);
                }
            }
        }

        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Equidistant Azimuthal";
    }
}
