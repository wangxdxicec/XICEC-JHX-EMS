package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.Projection;
import java.awt.geom.Point2D.Double;

public class LinearProjection extends Projection {
    public LinearProjection() {
    }

    public Double transform(Double src, Double dst) {
        dst.x = src.x;
        dst.y = src.y;
        return dst;
    }

    public void transform(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        for(int i = 0; i < numPoints; ++i) {
            dstPoints[dstOffset++] = srcPoints[srcOffset++];
            dstPoints[dstOffset++] = srcPoints[srcOffset++];
        }

    }

    public Double inverseTransform(Double src, Double dst) {
        dst.x = src.x;
        dst.y = src.y;
        return dst;
    }

    public void inverseTransform(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        for(int i = 0; i < numPoints; ++i) {
            dstPoints[dstOffset++] = srcPoints[srcOffset++];
            dstPoints[dstOffset++] = srcPoints[srcOffset++];
        }

    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isRectilinear() {
        return true;
    }

    public String toString() {
        return "Linear";
    }
}
