package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.SimpleConicProjection;

public class Murdoch3Projection extends SimpleConicProjection {
    public Murdoch3Projection() {
        super(3);
    }

    public String toString() {
        return "Murdoch III";
    }
}
