package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.AngleFormat;
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Rectangle2D;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Ellipsoid;
import java.text.FieldPosition;

public class Projection implements Cloneable {
    protected double minLatitude = -1.5707963267948966D;
    protected double minLongitude = -3.141592653589793D;
    protected double maxLatitude = 1.5707963267948966D;
    protected double maxLongitude = 3.141592653589793D;
    protected double projectionLatitude = 0.0D;
    protected double projectionLongitude = 0.0D;
    protected double projectionLatitude1 = 0.0D;
    protected double projectionLatitude2 = 0.0D;
    protected double scaleFactor = 1.0D;
    protected double falseEasting = 0.0D;
    protected double falseNorthing = 0.0D;
    protected double trueScaleLatitude = 0.0D;
    protected double a = 0.0D;
    protected double e = 0.0D;
    protected double es = 0.0D;
    protected double one_es = 0.0D;
    protected double rone_es = 0.0D;
    protected Ellipsoid ellipsoid;
    protected boolean spherical;
    protected boolean geocentric;
    protected String name = null;
    protected double fromMetres = 1.0D;
    private double totalScale = 0.0D;
    private double totalFalseEasting = 0.0D;
    private double totalFalseNorthing = 0.0D;
    protected static final double EPS10 = 1.0E-10D;
    protected static final double RTD = 57.29577951308232D;
    protected static final double DTR = 0.017453292519943295D;

    protected Projection() {
        this.setEllipsoid(Ellipsoid.SPHERE);
    }

    public Object clone() {
        try {
            Projection e = (Projection)super.clone();
            return e;
        } catch (CloneNotSupportedException var2) {
            throw new InternalError();
        }
    }

    public Double transform(Double src, Double dst) {
        double x = src.x * 0.017453292519943295D;
        if(this.projectionLongitude != 0.0D) {
            x = MapMath.normalizeLongitude(x - this.projectionLongitude);
        }

        this.project(x, src.y * 0.017453292519943295D, dst);
        dst.x = this.totalScale * dst.x + this.totalFalseEasting;
        dst.y = this.totalScale * dst.y + this.totalFalseNorthing;
        return dst;
    }

    public Double transformRadians(Double src, Double dst) {
        double x = src.x;
        if(this.projectionLongitude != 0.0D) {
            x = MapMath.normalizeLongitude(x - this.projectionLongitude);
        }

        this.project(x, src.y, dst);
        dst.x = this.totalScale * dst.x + this.totalFalseEasting;
        dst.y = this.totalScale * dst.y + this.totalFalseNorthing;
        return dst;
    }

    public Double project(double x, double y, Double dst) {
        dst.x = x;
        dst.y = y;
        return dst;
    }

    public void transform(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Double in = new Double();
        Double out = new Double();

        for(int i = 0; i < numPoints; ++i) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            this.transform(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }

    }

    public void transformRadians(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Double in = new Double();
        Double out = new Double();

        for(int i = 0; i < numPoints; ++i) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            this.transform(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }

    }

    public Double inverseTransform(Double src, Double dst) {
        double x = (src.x - this.totalFalseEasting) / this.totalScale;
        double y = (src.y - this.totalFalseNorthing) / this.totalScale;
        this.projectInverse(x, y, dst);
        if(dst.x < -3.141592653589793D) {
            dst.x = -3.141592653589793D;
        } else if(dst.x > 3.141592653589793D) {
            dst.x = 3.141592653589793D;
        }

        if(this.projectionLongitude != 0.0D) {
            dst.x = MapMath.normalizeLongitude(dst.x + this.projectionLongitude);
        }

        dst.x *= 57.29577951308232D;
        dst.y *= 57.29577951308232D;
        return dst;
    }

    public Double inverseTransformRadians(Double src, Double dst) {
        double x = (src.x - this.totalFalseEasting) / this.totalScale;
        double y = (src.y - this.totalFalseNorthing) / this.totalScale;
        this.projectInverse(x, y, dst);
        if(dst.x < -3.141592653589793D) {
            dst.x = -3.141592653589793D;
        } else if(dst.x > 3.141592653589793D) {
            dst.x = 3.141592653589793D;
        }

        if(this.projectionLongitude != 0.0D) {
            dst.x = MapMath.normalizeLongitude(dst.x + this.projectionLongitude);
        }

        return dst;
    }

    public Double projectInverse(double x, double y, Double dst) {
        dst.x = x;
        dst.y = y;
        return dst;
    }

    public void inverseTransform(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Double in = new Double();
        Double out = new Double();

        for(int i = 0; i < numPoints; ++i) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            this.inverseTransform(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }

    }

    public void inverseTransformRadians(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Double in = new Double();
        Double out = new Double();

        for(int i = 0; i < numPoints; ++i) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            this.inverseTransformRadians(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }

    }

    public Rectangle2D inverseTransform(Rectangle2D r) {
        Double in = new Double();
        Double out = new Double();
        Rectangle2D.Double bounds = null;
        int ix;
        double x;
        int iy;
        double y;
        if(this.isRectilinear()) {
            for(ix = 0; ix < 2; ++ix) {
                x = r.getX() + r.getWidth() * (double)ix;

                for(iy = 0; iy < 2; ++iy) {
                    y = r.getY() + r.getHeight() * (double)iy;
                    in.x = x;
                    in.y = y;
                    this.inverseTransform(in, out);
                    if(ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0.0D, 0.0D);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        } else {
            for(ix = 0; ix < 7; ++ix) {
                x = r.getX() + r.getWidth() * (double)ix / 6.0D;

                for(iy = 0; iy < 7; ++iy) {
                    y = r.getY() + r.getHeight() * (double)iy / 6.0D;
                    in.x = x;
                    in.y = y;
                    this.inverseTransform(in, out);
                    if(ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0.0D, 0.0D);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        }

        return bounds;
    }

    public Rectangle2D transform(Rectangle2D r) {
        Double in = new Double();
        Double out = new Double();
        Rectangle2D.Double bounds = null;
        int ix;
        double x;
        int iy;
        double y;
        if(this.isRectilinear()) {
            for(ix = 0; ix < 2; ++ix) {
                x = r.getX() + r.getWidth() * (double)ix;

                for(iy = 0; iy < 2; ++iy) {
                    y = r.getY() + r.getHeight() * (double)iy;
                    in.x = x;
                    in.y = y;
                    this.transform(in, out);
                    if(ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0.0D, 0.0D);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        } else {
            for(ix = 0; ix < 7; ++ix) {
                x = r.getX() + r.getWidth() * (double)ix / 6.0D;

                for(iy = 0; iy < 7; ++iy) {
                    y = r.getY() + r.getHeight() * (double)iy / 6.0D;
                    in.x = x;
                    in.y = y;
                    this.transform(in, out);
                    if(ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0.0D, 0.0D);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        }

        return bounds;
    }

    public boolean isConformal() {
        return false;
    }

    public boolean isEqualArea() {
        return false;
    }

    public boolean hasInverse() {
        return false;
    }

    public boolean isRectilinear() {
        return false;
    }

    public boolean parallelsAreParallel() {
        return this.isRectilinear();
    }

    public boolean inside(double x, double y) {
        x = (double)normalizeLongitude((float)(x * 0.017453292519943295D - this.projectionLongitude));
        return this.minLongitude <= x && x <= this.maxLongitude && this.minLatitude <= y && y <= this.maxLatitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name != null?this.name:this.toString();
    }

    public String getPROJ4Description() {
        AngleFormat format = new AngleFormat("DdM", false);
        StringBuffer sb = new StringBuffer();
        sb.append("+proj=" + this.getName() + " +a=" + this.a);
        if(this.es != 0.0D) {
            sb.append(" +es=" + this.es);
        }

        sb.append(" +lon_0=");
        format.format(this.projectionLongitude, sb, (FieldPosition)null);
        sb.append(" +lat_0=");
        format.format(this.projectionLatitude, sb, (FieldPosition)null);
        if(this.falseEasting != 1.0D) {
            sb.append(" +x_0=" + this.falseEasting);
        }

        if(this.falseNorthing != 1.0D) {
            sb.append(" +y_0=" + this.falseNorthing);
        }

        if(this.scaleFactor != 1.0D) {
            sb.append(" +k=" + this.scaleFactor);
        }

        if(this.fromMetres != 1.0D) {
            sb.append(" +fr_meters=" + this.fromMetres);
        }

        return sb.toString();
    }

    public String toString() {
        return "None";
    }

    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMinLatitude() {
        return this.minLatitude;
    }

    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public double getMaxLatitude() {
        return this.maxLatitude;
    }

    public double getMaxLatitudeDegrees() {
        return this.maxLatitude * 57.29577951308232D;
    }

    public double getMinLatitudeDegrees() {
        return this.minLatitude * 57.29577951308232D;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMinLongitude() {
        return this.minLongitude;
    }

    public void setMinLongitudeDegrees(double minLongitude) {
        this.minLongitude = 0.017453292519943295D * minLongitude;
    }

    public double getMinLongitudeDegrees() {
        return this.minLongitude * 57.29577951308232D;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public double getMaxLongitude() {
        return this.maxLongitude;
    }

    public void setMaxLongitudeDegrees(double maxLongitude) {
        this.maxLongitude = 0.017453292519943295D * maxLongitude;
    }

    public double getMaxLongitudeDegrees() {
        return this.maxLongitude * 57.29577951308232D;
    }

    public void setProjectionLatitude(double projectionLatitude) {
        this.projectionLatitude = projectionLatitude;
    }

    public double getProjectionLatitude() {
        return this.projectionLatitude;
    }

    public void setProjectionLatitudeDegrees(double projectionLatitude) {
        this.projectionLatitude = 0.017453292519943295D * projectionLatitude;
    }

    public double getProjectionLatitudeDegrees() {
        return this.projectionLatitude * 57.29577951308232D;
    }

    public void setProjectionLongitude(double projectionLongitude) {
        this.projectionLongitude = normalizeLongitudeRadians(projectionLongitude);
    }

    public double getProjectionLongitude() {
        return this.projectionLongitude;
    }

    public void setProjectionLongitudeDegrees(double projectionLongitude) {
        this.projectionLongitude = 0.017453292519943295D * projectionLongitude;
    }

    public double getProjectionLongitudeDegrees() {
        return this.projectionLongitude * 57.29577951308232D;
    }

    public void setTrueScaleLatitude(double trueScaleLatitude) {
        this.trueScaleLatitude = trueScaleLatitude;
    }

    public double getTrueScaleLatitude() {
        return this.trueScaleLatitude;
    }

    public void setTrueScaleLatitudeDegrees(double trueScaleLatitude) {
        this.trueScaleLatitude = 0.017453292519943295D * trueScaleLatitude;
    }

    public double getTrueScaleLatitudeDegrees() {
        return this.trueScaleLatitude * 57.29577951308232D;
    }

    public void setProjectionLatitude1(double projectionLatitude1) {
        this.projectionLatitude1 = projectionLatitude1;
    }

    public double getProjectionLatitude1() {
        return this.projectionLatitude1;
    }

    public void setProjectionLatitude1Degrees(double projectionLatitude1) {
        this.projectionLatitude1 = 0.017453292519943295D * projectionLatitude1;
    }

    public double getProjectionLatitude1Degrees() {
        return this.projectionLatitude1 * 57.29577951308232D;
    }

    public void setProjectionLatitude2(double projectionLatitude2) {
        this.projectionLatitude2 = projectionLatitude2;
    }

    public double getProjectionLatitude2() {
        return this.projectionLatitude2;
    }

    public void setProjectionLatitude2Degrees(double projectionLatitude2) {
        this.projectionLatitude2 = 0.017453292519943295D * projectionLatitude2;
    }

    public double getProjectionLatitude2Degrees() {
        return this.projectionLatitude2 * 57.29577951308232D;
    }

    public void setFalseNorthing(double falseNorthing) {
        this.falseNorthing = falseNorthing;
    }

    public double getFalseNorthing() {
        return this.falseNorthing;
    }

    public void setFalseEasting(double falseEasting) {
        this.falseEasting = falseEasting;
    }

    public double getFalseEasting() {
        return this.falseEasting;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public double getScaleFactor() {
        return this.scaleFactor;
    }

    public double getEquatorRadius() {
        return this.a;
    }

    public void setFromMetres(double fromMetres) {
        this.fromMetres = fromMetres;
    }

    public double getFromMetres() {
        return this.fromMetres;
    }

    public void setEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
        this.a = ellipsoid.equatorRadius;
        this.e = ellipsoid.eccentricity;
        this.es = ellipsoid.eccentricity2;
    }

    public Ellipsoid getEllipsoid() {
        return this.ellipsoid;
    }

    public int getEPSGCode() {
        return 0;
    }

    public void initialize() {
        this.spherical = this.e == 0.0D;
        this.one_es = 1.0D - this.es;
        this.rone_es = 1.0D / this.one_es;
        this.totalScale = this.a * this.fromMetres;
        this.totalFalseEasting = this.falseEasting * this.fromMetres;
        this.totalFalseNorthing = this.falseNorthing * this.fromMetres;
    }

    public static float normalizeLongitude(float angle) {
        if(!java.lang.Double.isInfinite((double)angle) && !java.lang.Double.isNaN((double)angle)) {
            while(angle > 180.0F) {
                angle -= 360.0F;
            }

            while(angle < -180.0F) {
                angle += 360.0F;
            }

            return angle;
        } else {
            throw new IllegalArgumentException("Infinite longitude");
        }
    }

    public static double normalizeLongitudeRadians(double angle) {
        if(!java.lang.Double.isInfinite(angle) && !java.lang.Double.isNaN(angle)) {
            while(angle > 3.141592653589793D) {
                angle -= 6.283185307179586D;
            }

            while(angle < -3.141592653589793D) {
                angle += 6.283185307179586D;
            }

            return angle;
        } else {
            throw new IllegalArgumentException("Infinite longitude");
        }
    }
}
