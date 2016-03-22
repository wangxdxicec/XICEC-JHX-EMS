package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.CylindricalProjection;

public class PseudoCylindricalProjection extends CylindricalProjection {
    public PseudoCylindricalProjection() {
    }

    public boolean isRectilinear() {
        return false;
    }

    public String toString() {
        return "Pseudo Cylindrical";
    }
}
