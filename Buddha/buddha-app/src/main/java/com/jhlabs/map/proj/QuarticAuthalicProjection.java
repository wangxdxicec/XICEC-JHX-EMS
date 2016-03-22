package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.STSProjection;

public class QuarticAuthalicProjection extends STSProjection {
    public QuarticAuthalicProjection() {
        super(2.0D, 2.0D, false);
    }

    public String toString() {
        return "Quartic Authalic";
    }
}
