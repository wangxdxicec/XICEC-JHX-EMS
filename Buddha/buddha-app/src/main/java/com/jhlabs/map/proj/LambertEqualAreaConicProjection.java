package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.AlbersProjection;

public class LambertEqualAreaConicProjection extends AlbersProjection {
    public LambertEqualAreaConicProjection() {
        this(false);
    }

    public LambertEqualAreaConicProjection(boolean south) {
        this.minLatitude = Math.toRadians(0.0D);
        this.maxLatitude = Math.toRadians(90.0D);
        this.projectionLatitude1 = south?-0.7853981633974483D:0.7853981633974483D;
        this.projectionLatitude2 = south?-1.5707963267948966D:1.5707963267948966D;
        this.initialize();
    }

    public String toString() {
        return "Lambert Equal Area Conic";
    }
}
