package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.STSProjection;

public class KavraiskyVProjection extends STSProjection {
    public KavraiskyVProjection() {
        super(1.50488D, 1.35439D, false);
    }

    public String toString() {
        return "Kavraisky V";
    }
}
