package com.jhlabs.map;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.AngleFormat;
import com.jhlabs.map.Unit;
import java.text.ParseException;

public class DegreeUnit extends Unit {
    static final long serialVersionUID = -3212757578604686538L;
    private static AngleFormat format = new AngleFormat("DdM", true);

    public DegreeUnit() {
        super("degree", "degrees", "deg", 1.0D);
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
}
