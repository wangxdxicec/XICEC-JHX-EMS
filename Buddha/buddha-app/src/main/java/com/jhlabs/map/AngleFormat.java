package com.jhlabs.map;

import com.jhlabs.map.MapMath;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by Administrator on 2016/3/21.
 */
public class AngleFormat extends NumberFormat {
    public static final String ddmmssPattern = "DdM";
    public static final String ddmmssPattern2 = "DdM\'S\"";
    public static final String ddmmssLongPattern = "DdM\'S\"W";
    public static final String ddmmssLatPattern = "DdM\'S\"N";
    public static final String ddmmssPattern4 = "DdMmSs";
    public static final String decimalPattern = "D.F";
    private DecimalFormat format;
    private String pattern;
    private boolean isDegrees;

    public AngleFormat() {
        this("DdM");
    }

    public AngleFormat(String pattern) {
        this(pattern, false);
    }

    public AngleFormat(String pattern, boolean isDegrees) {
        this.pattern = pattern;
        this.isDegrees = isDegrees;
        this.format = new DecimalFormat();
        this.format.setMaximumFractionDigits(0);
        this.format.setGroupingUsed(false);
    }

    public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
        return this.format((double)number, result, fieldPosition);
    }

    public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
        int length = this.pattern.length();
        boolean negative = false;
        if(number < 0.0D) {
            for(int ddmmss = length - 1; ddmmss >= 0; --ddmmss) {
                char c = this.pattern.charAt(ddmmss);
                if(c == 87 || c == 78) {
                    number = -number;
                    negative = true;
                    break;
                }
            }
        }

        double var14 = this.isDegrees?number:Math.toDegrees(number);
        int iddmmss = (int)Math.round(var14 * 3600.0D);
        if(iddmmss < 0) {
            iddmmss = -iddmmss;
        }

        int fraction = iddmmss % 3600;

        for(int i = 0; i < length; ++i) {
            char c1 = this.pattern.charAt(i);
            int f;
            switch(c1) {
                case 'D':
                    result.append((int)var14);
                    break;
                case 'F':
                    result.append(fraction);
                    break;
                case 'M':
                    f = fraction / 60;
                    if(f < 10) {
                        result.append('0');
                    }

                    result.append(f);
                    break;
                case 'N':
                    if(negative) {
                        result.append('S');
                    } else {
                        result.append('N');
                    }
                    break;
                case 'R':
                    result.append(number);
                    break;
                case 'S':
                    f = fraction % 60;
                    if(f < 10) {
                        result.append('0');
                    }

                    result.append(f);
                    break;
                case 'W':
                    if(negative) {
                        result.append('W');
                    } else {
                        result.append('E');
                    }
                    break;
                default:
                    result.append(c1);
            }
        }

        return result;
    }

    public Number parse(String text, ParsePosition parsePosition) {
        double d = 0.0D;
        double m = 0.0D;
        double s = 0.0D;
        boolean negate = false;
        int length = text.length();
        if(length > 0) {
            char i = Character.toUpperCase(text.charAt(length - 1));
            switch(i) {
                case 'S':
                case 'W':
                    negate = true;
                case 'E':
                case 'N':
                    text = text.substring(0, length - 1);
            }
        }

        int i1 = text.indexOf(100);
        if(i1 == -1) {
            i1 = text.indexOf(176);
        }

        double result;
        if(i1 != -1) {
            String dd = text.substring(0, i1);
            String mmss = text.substring(i1 + 1);
            d = Double.valueOf(dd).doubleValue();
            i1 = mmss.indexOf(109);
            if(i1 == -1) {
                i1 = mmss.indexOf(39);
            }

            if(i1 != -1) {
                String ss;
                if(i1 != 0) {
                    ss = mmss.substring(0, i1);
                    m = Double.valueOf(ss).doubleValue();
                }

                if(mmss.endsWith("s") || mmss.endsWith("\"")) {
                    mmss = mmss.substring(0, mmss.length() - 1);
                }

                if(i1 != mmss.length() - 1) {
                    ss = mmss.substring(i1 + 1);
                    s = Double.valueOf(ss).doubleValue();
                }

                if(m < 0.0D || m > 59.0D) {
                    throw new NumberFormatException("Minutes must be between 0 and 59");
                }

                if(s < 0.0D || s >= 60.0D) {
                    throw new NumberFormatException("Seconds must be between 0 and 59");
                }
            } else if(i1 != 0) {
                m = Double.valueOf(mmss).doubleValue();
            }

            if(this.isDegrees) {
                result = MapMath.dmsToDeg(d, m, s);
            } else {
                result = MapMath.dmsToRad(d, m, s);
            }
        } else {
            result = Double.parseDouble(text);
            if(!this.isDegrees) {
                result = Math.toRadians(result);
            }
        }

        if(parsePosition != null) {
            parsePosition.setIndex(text.length());
        }

        if(negate) {
            result = -result;
        }

        return new Double(result);
    }
}
