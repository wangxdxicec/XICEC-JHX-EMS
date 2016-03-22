package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.proj.Projection;

public class AzimuthalProjection extends Projection {
    public static final int NORTH_POLE = 1;
    public static final int SOUTH_POLE = 2;
    public static final int EQUATOR = 3;
    public static final int OBLIQUE = 4;
    protected int mode;
    protected double sinphi0;
    protected double cosphi0;
    private double mapRadius;

    public AzimuthalProjection() {
        this(Math.toRadians(45.0D), Math.toRadians(45.0D));
    }

    public AzimuthalProjection(double projectionLatitude, double projectionLongitude) {
        this.mapRadius = 90.0D;
        this.projectionLatitude = projectionLatitude;
        this.projectionLongitude = projectionLongitude;
        this.initialize();
    }

    public void initialize() {
        super.initialize();
        if(Math.abs(Math.abs(this.projectionLatitude) - 1.5707963267948966D) < 1.0E-10D) {
            this.mode = this.projectionLatitude < 0.0D?2:1;
        } else if(Math.abs(this.projectionLatitude) > 1.0E-10D) {
            this.mode = 4;
            this.sinphi0 = Math.sin(this.projectionLatitude);
            this.cosphi0 = Math.cos(this.projectionLatitude);
        } else {
            this.mode = 3;
        }

    }

    public boolean inside(double lon, double lat) {
        return MapMath.greatCircleDistance(Math.toRadians(lon), Math.toRadians(lat), this.projectionLongitude, this.projectionLatitude) < Math.toRadians(this.mapRadius);
    }

    public void setMapRadius(double mapRadius) {
        this.mapRadius = mapRadius;
    }

    public double getMapRadius() {
        return this.mapRadius;
    }
}
