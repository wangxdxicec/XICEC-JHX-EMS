package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.MapMath;
import com.jhlabs.map.Point2D.Double;
import com.jhlabs.map.proj.Projection;

public class Eckert4Projection extends Projection {
    private static final double C_x = 0.4222382003157712D;
    private static final double C_y = 1.3265004281770023D;
    private static final double RC_y = 0.7538633073600218D;
    private static final double C_p = 3.5707963267948966D;
    private static final double RC_p = 0.2800495767557787D;
    private static final double EPS = 1.0E-7D;
    private final int NITER = 6;

    public Eckert4Projection() {
    }

    public Double project(double lplam, double lpphi, Double out) {
        double p = 3.5707963267948966D * Math.sin(lpphi);
        double V = lpphi * lpphi;
        lpphi *= 0.895168D + V * (0.0218849D + V * 0.00826809D);

        int i;
        for(i = 6; i > 0; --i) {
            double c = Math.cos(lpphi);
            double s = Math.sin(lpphi);
            lpphi -= V = (lpphi + s * (c + 2.0D) - p) / (1.0D + c * (c + 2.0D) - s * s);
            if(Math.abs(V) < 1.0E-7D) {
                break;
            }
        }

        if(i == 0) {
            out.x = 0.4222382003157712D * lplam;
            out.y = lpphi < 0.0D?-1.3265004281770023D:1.3265004281770023D;
        } else {
            out.x = 0.4222382003157712D * lplam * (1.0D + Math.cos(lpphi));
            out.y = 1.3265004281770023D * Math.sin(lpphi);
        }

        return out;
    }

    public Double projectInverse(double xyx, double xyy, Double out) {
        out.y = MapMath.asin(xyy / 1.3265004281770023D);
        double c;
        out.x = xyx / (0.4222382003157712D * (1.0D + (c = Math.cos(out.y))));
        out.y = MapMath.asin((out.y + Math.sin(out.y) * (c + 2.0D)) / 3.5707963267948966D);
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Eckert IV";
    }
}
