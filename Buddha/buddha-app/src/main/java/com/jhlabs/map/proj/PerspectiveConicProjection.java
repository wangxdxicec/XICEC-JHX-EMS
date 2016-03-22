package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.SimpleConicProjection;

public class PerspectiveConicProjection extends SimpleConicProjection {
    public PerspectiveConicProjection() {
        super(4);
    }

    public String toString() {
        return "Perspective Conic";
    }
}
