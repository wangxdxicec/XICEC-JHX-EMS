package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.Projection;

public class CylindricalProjection extends Projection {
    public CylindricalProjection() {
    }

    public boolean isRectilinear() {
        return true;
    }

    public String toString() {
        return "Cylindrical";
    }
}
