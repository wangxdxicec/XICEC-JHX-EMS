package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.SimpleConicProjection;

public class Murdoch1Projection extends SimpleConicProjection {
    public Murdoch1Projection() {
        super(1);
    }

    public String toString() {
        return "Murdoch I";
    }
}
