package com.jhlabs.map;

/**
 * Created by Administrator on 2016/3/21.
 */
public abstract class Rectangle2D {
    public Rectangle2D() {
    }

    public abstract double getX();

    public abstract double getY();

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract void add(double var1, double var3);

    public static class Double extends Rectangle2D {
        double x;
        double y;
        double w;
        double h;

        public Double(double xIn, double yIn, double wIn, double hIn) {
            this.x = xIn;
            this.y = yIn;
            this.w = wIn;
            this.h = hIn;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getWidth() {
            return this.w;
        }

        public double getHeight() {
            return this.h;
        }

        public void add(double px, double py) {
            if(px < this.x) {
                this.x = px;
            } else if(px > this.x + this.w) {
                this.w = px - this.x;
            }

            if(py < this.y) {
                this.y = py;
            } else if(py > this.y + this.h) {
                this.h = py - this.y;
            }

        }
    }
}
