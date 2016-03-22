package com.jhlabs.map;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.DegreeUnit;
import com.jhlabs.map.Unit;

public class Units {
    public static final Unit DEGREES = new DegreeUnit();
    public static final Unit RADIANS = new Unit("radian", "radians", "rad", Math.toDegrees(1.0D));
    public static final Unit ARC_MINUTES = new Unit("arc minute", "arc minutes", "min", 0.016666666666666666D);
    public static final Unit ARC_SECONDS = new Unit("arc second", "arc seconds", "sec", 2.777777777777778E-4D);
    public static final Unit KILOMETRES = new Unit("kilometre", "kilometres", "km", 1000.0D);
    public static final Unit METRES = new Unit("metre", "metres", "m", 1.0D);
    public static final Unit DECIMETRES = new Unit("decimetre", "decimetres", "dm", 0.1D);
    public static final Unit CENTIMETRES = new Unit("centimetre", "centimetres", "cm", 0.01D);
    public static final Unit MILLIMETRES = new Unit("millimetre", "millimetres", "mm", 0.001D);
    public static final Unit NAUTICAL_MILES = new Unit("nautical mile", "nautical miles", "kmi", 1852.0D);
    public static final Unit MILES = new Unit("mile", "miles", "mi", 1609.344D);
    public static final Unit CHAINS = new Unit("chain", "chains", "ch", 20.1168D);
    public static final Unit YARDS = new Unit("yard", "yards", "yd", 0.9144D);
    public static final Unit FEET = new Unit("foot", "feet", "ft", 0.3048D);
    public static final Unit INCHES = new Unit("inch", "inches", "in", 0.0254D);
    public static final Unit US_MILES = new Unit("U.S. mile", "U.S. miles", "us-mi", 1609.347218694437D);
    public static final Unit US_CHAINS = new Unit("U.S. chain", "U.S. chains", "us-ch", 20.11684023368047D);
    public static final Unit US_YARDS = new Unit("U.S. yard", "U.S. yards", "us-yd", 0.914401828803658D);
    public static final Unit US_FEET = new Unit("U.S. foot", "U.S. feet", "us-ft", 0.304800609601219D);
    public static final Unit US_INCHES = new Unit("U.S. inch", "U.S. inches", "us-in", 0.025400050800101603D);
    public static final Unit FATHOMS = new Unit("fathom", "fathoms", "fath", 1.8288D);
    public static final Unit LINKS = new Unit("link", "links", "link", 0.201168D);
    public static final Unit POINTS = new Unit("point", "points", "point", 3.5145980351459805E-4D);
    public static Unit[] units;

    static {
        units = new Unit[]{DEGREES, KILOMETRES, METRES, DECIMETRES, CENTIMETRES, MILLIMETRES, MILES, YARDS, FEET, INCHES, US_MILES, US_YARDS, US_FEET, US_INCHES, NAUTICAL_MILES};
    }

    public Units() {
    }

    public static Unit findUnits(String name) {
        for(int i = 0; i < units.length; ++i) {
            if(name.equals(units[i].name) || name.equals(units[i].plural) || name.equals(units[i].abbreviation)) {
                return units[i];
            }
        }

        return METRES;
    }

    public static double convert(double value, Unit from, Unit to) {
        return from == to?value:to.fromBase(from.toBase(value));
    }
}
