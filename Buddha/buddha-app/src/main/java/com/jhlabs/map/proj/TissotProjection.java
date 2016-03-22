package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.SimpleConicProjection;

public class TissotProjection extends SimpleConicProjection {
    public TissotProjection() {
        super(5);
    }

    public String toString() {
        return "Tissot";
    }
}
