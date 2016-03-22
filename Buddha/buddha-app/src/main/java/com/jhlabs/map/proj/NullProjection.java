package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.Projection;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;

public class NullProjection extends Projection {
    public NullProjection() {
    }

    public Double transform(Double src, Double dst) {
        dst.x = src.x;
        dst.y = src.y;
        return dst;
    }

    public Shape projectPath(Shape path, AffineTransform t, boolean filled) {
        if(t != null) {
            t.createTransformedShape(path);
        }

        return path;
    }

    public Shape getBoundingShape() {
        return null;
    }

    public boolean isRectilinear() {
        return true;
    }

    public String toString() {
        return "Null";
    }
}
