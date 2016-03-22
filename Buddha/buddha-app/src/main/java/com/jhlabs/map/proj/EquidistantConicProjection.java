package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.ConicProjection;

public class EquidistantConicProjection extends ConicProjection {
    private double standardLatitude1;
    private double standardLatitude2;
    private double eccentricity = 0.822719D;
    private double eccentricity2;
    private double eccentricity4;
    private double eccentricity6;
    private double radius;
    private boolean northPole;
    private double f;
    private double n;
    private double rho0;

    public EquidistantConicProjection() {
        this.eccentricity2 = this.eccentricity * this.eccentricity;
        this.eccentricity4 = this.eccentricity2 * this.eccentricity2;
        this.eccentricity6 = this.eccentricity2 * this.eccentricity4;
        this.radius = 1.0D;
        this.minLatitude = MapMath.degToRad(10.0D);
        this.maxLatitude = MapMath.degToRad(70.0D);
        this.minLongitude = MapMath.degToRad(-90.0D);
        this.maxLongitude = MapMath.degToRad(90.0D);
        this.standardLatitude1 = Math.toDegrees(60.0D);
        this.standardLatitude2 = Math.toDegrees(20.0D);
        this.initialize(MapMath.degToRad(0.0D), MapMath.degToRad(37.5D), this.standardLatitude1, this.standardLatitude2);
    }

    public Double transform(Double in, Double out) {
        double lon = MapMath.normalizeLongitude(in.x - this.projectionLongitude);
        double lat = in.y;
        double hold2 = Math.pow((1.0D - this.eccentricity * Math.sin(lat)) / (1.0D + this.eccentricity * Math.sin(lat)), 0.5D * this.eccentricity);
        double hold3 = Math.tan(0.7853981633974483D - 0.5D * lat);
        double hold1 = hold3 == 0.0D?0.0D:Math.pow(hold3 / hold2, this.n);
        double rho = this.radius * this.f * hold1;
        double theta = this.n * lon;
        out.x = rho * Math.sin(theta);
        out.y = this.rho0 - rho * Math.cos(theta);
        return out;
    }

    public Double inverseTransform(Double in, Double out) {
        double phi = 0.0D;
        double theta = Math.atan(in.x / (this.rho0 - in.y));
        out.x = theta / this.n + this.projectionLongitude;
        double temp = in.x * in.x + (this.rho0 - in.y) * (this.rho0 - in.y);
        double rho = Math.sqrt(temp);
        if(this.n < 0.0D) {
            rho = -rho;
        }

        double t = Math.pow(rho / (this.radius * this.f), 1.0D / this.n);
        double tphi = 1.5707963267948966D - 2.0D * Math.atan(t);
        double delta = 1.0D;

        for(int i = 0; i < 100 && delta > 1.0E-8D; ++i) {
            temp = (1.0D - this.eccentricity * Math.sin(tphi)) / (1.0D + this.eccentricity * Math.sin(tphi));
            phi = 1.5707963267948966D - 2.0D * Math.atan(t * Math.pow(temp, 0.5D * this.eccentricity));
            delta = Math.abs(Math.abs(tphi) - Math.abs(phi));
            tphi = phi;
        }

        out.y = phi;
        return out;
    }

    private void initialize(double rlong0, double rlat0, double standardLatitude1, double standardLatitude2) {
        super.initialize();
        this.northPole = rlat0 > 0.0D;
        this.projectionLatitude = this.northPole?1.5707963267948966D:-1.5707963267948966D;
        double t_standardLatitude1 = Math.tan(0.7853981633974483D - 0.5D * standardLatitude1) / Math.pow((1.0D - this.eccentricity * Math.sin(standardLatitude1)) / (1.0D + this.eccentricity * Math.sin(standardLatitude1)), 0.5D * this.eccentricity);
        double m_standardLatitude1 = Math.cos(standardLatitude1) / Math.sqrt(1.0D - this.eccentricity2 * Math.pow(Math.sin(standardLatitude1), 2.0D));
        double t_standardLatitude2 = Math.tan(0.7853981633974483D - 0.5D * standardLatitude2) / Math.pow((1.0D - this.eccentricity * Math.sin(standardLatitude2)) / (1.0D + this.eccentricity * Math.sin(standardLatitude2)), 0.5D * this.eccentricity);
        double m_standardLatitude2 = Math.cos(standardLatitude2) / Math.sqrt(1.0D - this.eccentricity2 * Math.pow(Math.sin(standardLatitude2), 2.0D));
        double t_rlat0 = Math.tan(0.7853981633974483D - 0.5D * rlat0) / Math.pow((1.0D - this.eccentricity * Math.sin(rlat0)) / (1.0D + this.eccentricity * Math.sin(rlat0)), 0.5D * this.eccentricity);
        if(standardLatitude1 != standardLatitude2) {
            this.n = (Math.log(m_standardLatitude1) - Math.log(m_standardLatitude2)) / (Math.log(t_standardLatitude1) - Math.log(t_standardLatitude2));
        } else {
            this.n = Math.sin(standardLatitude1);
        }

        this.f = m_standardLatitude1 / (this.n * Math.pow(t_standardLatitude1, this.n));
        this.projectionLongitude = rlong0;
        this.rho0 = this.radius * this.f * Math.pow(t_rlat0, this.n);
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Equidistant Conic";
    }
}
