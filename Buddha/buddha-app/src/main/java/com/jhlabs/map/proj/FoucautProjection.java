package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.STSProjection;

public class FoucautProjection extends STSProjection {
    public FoucautProjection() {
        super(2.0D, 2.0D, true);
    }

    public String toString() {
        return "Foucaut";
    }
}
