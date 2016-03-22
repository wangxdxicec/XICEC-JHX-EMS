package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.SimpleConicProjection;

public class EulerProjection extends SimpleConicProjection {
    public EulerProjection() {
        super(0);
    }

    public String toString() {
        return "Euler";
    }
}
