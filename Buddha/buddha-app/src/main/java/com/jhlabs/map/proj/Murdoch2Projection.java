package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.SimpleConicProjection;

public class Murdoch2Projection extends SimpleConicProjection {
    public Murdoch2Projection() {
        super(2);
    }

    public String toString() {
        return "Murdoch II";
    }
}
