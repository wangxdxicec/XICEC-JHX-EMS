package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.CylindricalProjection;

public class PlateCarreeProjection extends CylindricalProjection {
    public PlateCarreeProjection() {
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isRectilinear() {
        return true;
    }

    public String toString() {
        return "Plate Carrée";
    }
}
