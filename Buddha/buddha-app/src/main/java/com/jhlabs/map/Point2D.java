package com.jhlabs.map;

/**
 * Created by Administrator on 2016/3/21.
 */
public class Point2D {
    public Point2D() {
    }

    public static class Double {
        public double x;
        public double y;

        public Double() {
            this.x = this.y = 0.0D;
        }

        public Double(double xIn, double yIn) {
            this.x = xIn;
            this.y = yIn;
        }

        public String toString() {
            return "com.jhlabs.map.Point2D.Double: x=" + this.x + " y=" + this.y;
        }
    }
}
