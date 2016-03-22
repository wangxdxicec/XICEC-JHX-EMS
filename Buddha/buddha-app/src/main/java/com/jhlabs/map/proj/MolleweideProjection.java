package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.PseudoCylindricalProjection;

public class MolleweideProjection extends PseudoCylindricalProjection {
    public static final int MOLLEWEIDE = 0;
    public static final int WAGNER4 = 1;
    public static final int WAGNER5 = 2;
    private static final int MAX_ITER = 10;
    private static final double TOLERANCE = 1.0E-7D;
    private int type;
    private double cx;
    private double cy;
    private double cp;

    public MolleweideProjection() {
        this(1.5707963267948966D);
    }

    public MolleweideProjection(int type) {
        this.type = 0;
        this.type = type;
        switch(type) {
            case 0:
                this.init(1.5707963267948966D);
                break;
            case 1:
                this.init(1.0471975511965976D);
                break;
            case 2:
                this.init(1.5707963267948966D);
                this.cx = 0.90977D;
                this.cy = 1.65014D;
                this.cp = 3.00896D;
        }

    }

    public MolleweideProjection(double p) {
        this.type = 0;
        this.init(p);
    }

    public void init(double p) {
        double p2 = p + p;
        double sp = Math.sin(p);
        double r = Math.sqrt(6.283185307179586D * sp / (p2 + Math.sin(p2)));
        this.cx = 2.0D * r / 3.141592653589793D;
        this.cy = r / sp;
        this.cp = p2 + Math.sin(p2);
    }

    public MolleweideProjection(double cx, double cy, double cp) {
        this.type = 0;
        this.cx = cx;
        this.cy = cy;
        this.cp = cp;
    }

    public Double project(double lplam, double lpphi, Double xy) {
        double k = this.cp * Math.sin(lpphi);

        int i;
        for(i = 10; i != 0; --i) {
            double v;
            lpphi -= v = (lpphi + Math.sin(lpphi) - k) / (1.0D + Math.cos(lpphi));
            if(Math.abs(v) < 1.0E-7D) {
                break;
            }
        }

        if(i == 0) {
            lpphi = lpphi < 0.0D?-1.5707963267948966D:1.5707963267948966D;
        } else {
            lpphi *= 0.5D;
        }

        xy.x = this.cx * lplam * Math.cos(lpphi);
        xy.y = this.cy * Math.sin(lpphi);
        return xy;
    }

    public Double projectInverse(double x, double y, Double lp) {
        double lat = Math.asin(y / this.cy);
        double lon = x / (this.cx * Math.cos(lat));
        lat += lat;
        lat = Math.asin((lat + Math.sin(lat)) / this.cp);
        lp.x = lon;
        lp.y = lat;
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        this.type = this.type;
        switch(this.type) {
            case 1:
                return "Wagner IV";
            case 2:
                return "Wagner V";
            default:
                return "Molleweide";
        }
    }
}
