package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.proj.MolleweideProjection;
import com.jhlabs.map.proj.SimpleConicProjection;

public class OtherProjections {
    public OtherProjections() {
    }

    public Object createPlugin() {
        return new Object[]{new SimpleConicProjection(5), new SimpleConicProjection(1), new SimpleConicProjection(2), new SimpleConicProjection(3), new SimpleConicProjection(4), new SimpleConicProjection(6), new MolleweideProjection(1), new MolleweideProjection(2)};
    }
}
