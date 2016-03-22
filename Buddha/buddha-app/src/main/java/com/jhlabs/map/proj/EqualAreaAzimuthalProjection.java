package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.AzimuthalProjection;
import com.jhlabs.map.proj.ProjectionException;

public class EqualAreaAzimuthalProjection extends AzimuthalProjection {
    private double sinb1;
    private double cosb1;
    private double xmf;
    private double ymf;
    private double mmf;
    private double qp;
    private double dd;
    private double rq;
    private double[] apa;

    public EqualAreaAzimuthalProjection() {
        this.initialize();
    }

    public Object clone() {
        EqualAreaAzimuthalProjection p = (EqualAreaAzimuthalProjection)super.clone();
        if(this.apa != null) {
            p.apa = (double[])this.apa.clone();
        }

        return p;
    }

    public void initialize() {
        super.initialize();
        if(this.spherical) {
            if(this.mode == 4) {
                this.sinphi0 = Math.sin(this.projectionLatitude);
                this.cosphi0 = Math.cos(this.projectionLatitude);
            }
        } else {
            this.qp = MapMath.qsfn(1.0D, this.e, this.one_es);
            this.mmf = 0.5D / (1.0D - this.es);
            this.apa = MapMath.authset(this.es);
            switch(this.mode) {
                case 1:
                case 2:
                    this.dd = 1.0D;
                    break;
                case 3:
                    this.dd = 1.0D / (this.rq = Math.sqrt(0.5D * this.qp));
                    this.xmf = 1.0D;
                    this.ymf = 0.5D * this.qp;
                    break;
                case 4:
                    this.rq = Math.sqrt(0.5D * this.qp);
                    double sinphi = Math.sin(this.projectionLatitude);
                    this.sinb1 = MapMath.qsfn(sinphi, this.e, this.one_es) / this.qp;
                    this.cosb1 = Math.sqrt(1.0D - this.sinb1 * this.sinb1);
                    this.dd = Math.cos(this.projectionLatitude) / (Math.sqrt(1.0D - this.es * sinphi * sinphi) * this.rq * this.cosb1);
                    this.ymf = (this.xmf = this.rq) / this.dd;
                    this.xmf *= this.dd;
            }
        }

    }

    public Double project(double lam, double phi, Double xy) {
        double coslam;
        double sinlam;
        double sinphi;
        if(this.spherical) {
            sinphi = Math.sin(phi);
            sinlam = Math.cos(phi);
            coslam = Math.cos(lam);
            switch(this.mode) {
                case 1:
                    coslam = -coslam;
                case 2:
                    if(Math.abs(phi + this.projectionLatitude) < 1.0E-10D) {
                        throw new ProjectionException();
                    }

                    xy.y = 0.7853981633974483D - phi * 0.5D;
                    xy.y = 2.0D * (this.mode == 2?Math.cos(xy.y):Math.sin(xy.y));
                    xy.x = xy.y * Math.sin(lam);
                    xy.y *= coslam;
                    break;
                case 3:
                    xy.y = 1.0D + sinlam * coslam;
                    if(xy.y <= 1.0E-10D) {
                        throw new ProjectionException();
                    }

                    xy.x = (xy.y = Math.sqrt(2.0D / xy.y)) * sinlam * Math.sin(lam);
                    xy.y *= this.mode == 3?sinphi:this.cosphi0 * sinphi - this.sinphi0 * sinlam * coslam;
                    break;
                case 4:
                    xy.y = 1.0D + this.sinphi0 * sinphi + this.cosphi0 * sinlam * coslam;
                    if(xy.y <= 1.0E-10D) {
                        throw new ProjectionException();
                    }

                    xy.x = (xy.y = Math.sqrt(2.0D / xy.y)) * sinlam * Math.sin(lam);
                    xy.y *= this.mode == 3?sinphi:this.cosphi0 * sinphi - this.sinphi0 * sinlam * coslam;
            }
        } else {
            double sinb = 0.0D;
            double cosb = 0.0D;
            double b = 0.0D;
            coslam = Math.cos(lam);
            sinlam = Math.sin(lam);
            sinphi = Math.sin(phi);
            double q = MapMath.qsfn(sinphi, this.e, this.one_es);
            if(this.mode == 4 || this.mode == 3) {
                sinb = q / this.qp;
                cosb = Math.sqrt(1.0D - sinb * sinb);
            }

            switch(this.mode) {
                case 1:
                    b = 1.5707963267948966D + phi;
                    q = this.qp - q;
                    break;
                case 2:
                    b = phi - 1.5707963267948966D;
                    q += this.qp;
                    break;
                case 3:
                    b = 1.0D + cosb * coslam;
                    break;
                case 4:
                    b = 1.0D + this.sinb1 * sinb + this.cosb1 * cosb * coslam;
            }

            if(Math.abs(b) < 1.0E-10D) {
                throw new ProjectionException();
            }

            switch(this.mode) {
                case 1:
                case 2:
                    if(q >= 0.0D) {
                        xy.x = (b = Math.sqrt(q)) * sinlam;
                        xy.y = coslam * (this.mode == 2?b:-b);
                    } else {
                        xy.x = xy.y = 0.0D;
                    }
                    break;
                case 3:
                    xy.y = (b = Math.sqrt(2.0D / (1.0D + cosb * coslam))) * sinb * this.ymf;
                    xy.x = this.xmf * b * cosb * sinlam;
                    break;
                case 4:
                    xy.y = this.ymf * (b = Math.sqrt(2.0D / b)) * (this.cosb1 * sinb - this.sinb1 * cosb * coslam);
                    xy.x = this.xmf * b * cosb * sinlam;
            }
        }

        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        double cCe;
        double sCe;
        double q;
        if(this.spherical) {
            cCe = 0.0D;
            q = 0.0D;
            sCe = MapMath.distance(x, y);
            if((lp.y = sCe * 0.5D) > 1.0D) {
                throw new ProjectionException();
            }

            lp.y = 2.0D * Math.asin(lp.y);
            if(this.mode == 4 || this.mode == 3) {
                q = Math.sin(lp.y);
                cCe = Math.cos(lp.y);
            }

            switch(this.mode) {
                case 1:
                    y = -y;
                    lp.y = 1.5707963267948966D - lp.y;
                    break;
                case 2:
                    --lp.y;
                    break;
                case 3:
                    lp.y = Math.abs(sCe) <= 1.0E-10D?0.0D:Math.asin(y * q / sCe);
                    x *= q;
                    y = cCe * sCe;
                    break;
                case 4:
                    lp.y = Math.abs(sCe) <= 1.0E-10D?this.projectionLatitude:Math.asin(cCe * this.sinphi0 + y * q * this.cosphi0 / sCe);
                    x *= q * this.cosphi0;
                    y = (cCe - Math.sin(lp.y) * this.sinphi0) * sCe;
            }

            lp.x = y != 0.0D || this.mode != 3 && this.mode != 4?Math.atan2(x, y):0.0D;
        } else {
            double ab = 0.0D;
            switch(this.mode) {
                case 1:
                    y = -y;
                case 2:
                    if((q = x * x + y * y) == 0.0D) {
                        lp.x = 0.0D;
                        lp.y = this.projectionLatitude;
                        return lp;
                    }

                    ab = 1.0D - q / this.qp;
                    if(this.mode == 2) {
                        ab = -ab;
                    }
                    break;
                case 3:
                case 4:
                    double rho;
                    if((rho = MapMath.distance(x /= this.dd, y *= this.dd)) < 1.0E-10D) {
                        lp.x = 0.0D;
                        lp.y = this.projectionLatitude;
                        return lp;
                    }

                    cCe = Math.cos(sCe = 2.0D * Math.asin(0.5D * rho / this.rq));
                    x *= sCe = Math.sin(sCe);
                    double var10000;
                    if(this.mode == 4) {
                        var10000 = this.qp * (ab = cCe * this.sinb1 + y * sCe * this.cosb1 / rho);
                        y = rho * this.cosb1 * cCe - y * this.sinb1 * sCe;
                    } else {
                        var10000 = this.qp * (ab = y * sCe / rho);
                        y = rho * cCe;
                    }
            }

            lp.x = Math.atan2(x, y);
            lp.y = MapMath.authlat(Math.asin(ab), this.apa);
        }

        return lp;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Lambert Equal Area Azimuthal";
    }
}
