package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;

public class AlbersProjection extends Projection {
    private static final double EPS10 = 1.0E-10D;
    private static final double TOL7 = 1.0E-7D;
    private double ec;
    private double n;
    private double c;
    private double dd;
    private double n2;
    private double rho0;
    private double phi1;
    private double phi2;
    private double[] en;
    private static final int N_ITER = 15;
    private static final double EPSILON = 1.0E-7D;
    private static final double TOL = 1.0E-10D;
    protected double projectionLatitude1 = MapMath.degToRad(45.5D);
    protected double projectionLatitude2 = MapMath.degToRad(29.5D);

    public AlbersProjection() {
        this.minLatitude = Math.toRadians(0.0D);
        this.maxLatitude = Math.toRadians(80.0D);
        this.initialize();
    }

    private static double phi1_(double qs, double Te, double Tone_es) {
        double Phi = Math.asin(0.5D * qs);
        if(Te < 1.0E-7D) {
            return Phi;
        } else {
            int i = 15;

            do {
                double sinpi = Math.sin(Phi);
                double cospi = Math.cos(Phi);
                double con = Te * sinpi;
                double com = 1.0D - con * con;
                double dphi = 0.5D * com * com / cospi * (qs / Tone_es - sinpi / com + 0.5D / Te * Math.log((1.0D - con) / (1.0D + con)));
                Phi += dphi;
                if(Math.abs(dphi) <= 1.0E-10D) {
                    break;
                }

                --i;
            } while(i != 0);

            return i != 0?Phi:1.7976931348623157E308D;
        }
    }

    public Double project(double lplam, double lpphi, Double out) {
        double rho;
        if((rho = this.c - (!this.spherical?this.n * MapMath.qsfn(Math.sin(lpphi), this.e, this.one_es):this.n2 * Math.sin(lpphi))) < 0.0D) {
            throw new ProjectionException("F");
        } else {
            rho = this.dd * Math.sqrt(rho);
            out.x = rho * Math.sin(lplam *= this.n);
            out.y = this.rho0 - rho * Math.cos(lplam);
            return out;
        }
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        double rho;
        if((rho = MapMath.distance(xyx, xyy = this.rho0 - xyy)) != 0.0D) {
            if(this.n < 0.0D) {
                rho = -rho;
                xyx = -xyx;
                xyy = -xyy;
            }

            double lpphi = rho / this.dd;
            if(!this.spherical) {
                lpphi = (this.c - lpphi * lpphi) / this.n;
                if(Math.abs(this.ec - Math.abs(lpphi)) > 1.0E-7D) {
                    if((lpphi = phi1_(lpphi, this.e, this.one_es)) == 1.7976931348623157E308D) {
                        throw new ProjectionException("I");
                    }
                } else {
                    lpphi = lpphi < 0.0D?-1.5707963267948966D:1.5707963267948966D;
                }
            } else if(Math.abs(out.y = (this.c - lpphi * lpphi) / this.n2) <= 1.0D) {
                lpphi = Math.asin(lpphi);
            } else {
                lpphi = lpphi < 0.0D?-1.5707963267948966D:1.5707963267948966D;
            }

            double lplam = Math.atan2(xyx, xyy) / this.n;
            out.x = lplam;
            out.y = lpphi;
        } else {
            out.x = 0.0D;
            out.y = this.n > 0.0D?1.5707963267948966D:-1.5707963267948966D;
        }

        return out;
    }

    public void initialize() {
        super.initialize();
        this.phi1 = this.projectionLatitude1;
        this.phi2 = this.projectionLatitude2;
        if(Math.abs(this.phi1 + this.phi2) < 1.0E-10D) {
            throw new IllegalArgumentException("-21");
        } else {
            double sinphi;
            this.n = sinphi = Math.sin(this.phi1);
            double cosphi = Math.cos(this.phi1);
            boolean secant = Math.abs(this.phi1 - this.phi2) >= 1.0E-10D;
            this.spherical = this.es > 0.0D;
            if(!this.spherical) {
                if((this.en = MapMath.enfn(this.es)) == null) {
                    throw new IllegalArgumentException("0");
                }

                double m1 = MapMath.msfn(sinphi, cosphi, this.es);
                double ml1 = MapMath.qsfn(sinphi, this.e, this.one_es);
                if(secant) {
                    sinphi = Math.sin(this.phi2);
                    cosphi = Math.cos(this.phi2);
                    double m2 = MapMath.msfn(sinphi, cosphi, this.es);
                    double ml2 = MapMath.qsfn(sinphi, this.e, this.one_es);
                    this.n = (m1 * m1 - m2 * m2) / (ml2 - ml1);
                }

                this.ec = 1.0D - 0.5D * this.one_es * Math.log((1.0D - this.e) / (1.0D + this.e)) / this.e;
                this.c = m1 * m1 + this.n * ml1;
                this.dd = 1.0D / this.n;
                this.rho0 = this.dd * Math.sqrt(this.c - this.n * MapMath.qsfn(Math.sin(this.projectionLatitude), this.e, this.one_es));
            } else {
                if(secant) {
                    this.n = 0.5D * (this.n + Math.sin(this.phi2));
                }

                this.n2 = this.n + this.n;
                this.c = cosphi * cosphi + this.n2 * sinphi;
                this.dd = 1.0D / this.n;
                this.rho0 = this.dd * Math.sqrt(this.c - this.n2 * Math.sin(this.projectionLatitude));
            }

        }
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public int getEPSGCode() {
        return 9822;
    }

    public String toString() {
        return "Albers Equal Area";
    }
}
