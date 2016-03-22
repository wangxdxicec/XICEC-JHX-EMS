package com.jhlabs.map;

/**
 * Created by Administrator on 2016/3/21.
 */
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

public class Unit implements Serializable {
    static final long serialVersionUID = -6704954923429734628L;
    public static final int ANGLE_UNIT = 0;
    public static final int LENGTH_UNIT = 1;
    public static final int AREA_UNIT = 2;
    public static final int VOLUME_UNIT = 3;
    public String name;
    public String plural;
    public String abbreviation;
    public double value;
    public static NumberFormat format = NumberFormat.getNumberInstance();

    static {
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(false);
    }

    public Unit(String name, String plural, String abbreviation, double value) {
        this.name = name;
        this.plural = plural;
        this.abbreviation = abbreviation;
        this.value = value;
    }

    public double toBase(double n) {
        return n * this.value;
    }

    public double fromBase(double n) {
        return n / this.value;
    }

    public double parse(String s) throws NumberFormatException {
        try {
            return format.parse(s).doubleValue();
        } catch (ParseException var3) {
            throw new NumberFormatException(var3.getMessage());
        }
    }

    public String format(double n) {
        return format.format(n) + " " + this.abbreviation;
    }

    public String format(double n, boolean abbrev) {
        return abbrev?format.format(n) + " " + this.abbreviation:format.format(n);
    }

    public String format(double x, double y, boolean abbrev) {
        return abbrev?format.format(x) + "/" + format.format(y) + " " + this.abbreviation:format.format(x) + "/" + format.format(y);
    }

    public String format(double x, double y) {
        return this.format(x, y, true);
    }

    public String toString() {
        return this.plural;
    }

    public boolean equals(Object o) {
        return o instanceof Unit?((Unit)o).value == this.value:false;
    }
}
