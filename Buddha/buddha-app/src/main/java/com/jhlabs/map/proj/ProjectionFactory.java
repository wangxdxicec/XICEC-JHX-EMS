package com.jhlabs.map.proj;

/**
 * Created by Administrator on 2016/3/21.
 */
import com.jhlabs.map.AngleFormat;
import com.jhlabs.map.Unit;
import com.jhlabs.map.Units;
import com.jhlabs.map.proj.AiryProjection;
import com.jhlabs.map.proj.AitoffProjection;
import com.jhlabs.map.proj.AlbersProjection;
import com.jhlabs.map.proj.AugustProjection;
import com.jhlabs.map.proj.BipolarProjection;
import com.jhlabs.map.proj.BoggsProjection;
import com.jhlabs.map.proj.BonneProjection;
import com.jhlabs.map.proj.CassiniProjection;
import com.jhlabs.map.proj.CentralCylindricalProjection;
import com.jhlabs.map.proj.CollignonProjection;
import com.jhlabs.map.proj.CrasterProjection;
import com.jhlabs.map.proj.DenoyerProjection;
import com.jhlabs.map.proj.Eckert1Projection;
import com.jhlabs.map.proj.Eckert2Projection;
import com.jhlabs.map.proj.Eckert4Projection;
import com.jhlabs.map.proj.Eckert5Projection;
import com.jhlabs.map.proj.Ellipsoid;
import com.jhlabs.map.proj.EquidistantAzimuthalProjection;
import com.jhlabs.map.proj.EquidistantConicProjection;
import com.jhlabs.map.proj.EulerProjection;
import com.jhlabs.map.proj.FaheyProjection;
import com.jhlabs.map.proj.FoucautProjection;
import com.jhlabs.map.proj.FoucautSinusoidalProjection;
import com.jhlabs.map.proj.GallProjection;
import com.jhlabs.map.proj.GnomonicAzimuthalProjection;
import com.jhlabs.map.proj.GoodeProjection;
import com.jhlabs.map.proj.HammerProjection;
import com.jhlabs.map.proj.HatanoProjection;
import com.jhlabs.map.proj.KavraiskyVProjection;
import com.jhlabs.map.proj.LagrangeProjection;
import com.jhlabs.map.proj.LambertConformalConicProjection;
import com.jhlabs.map.proj.LambertEqualAreaConicProjection;
import com.jhlabs.map.proj.LandsatProjection;
import com.jhlabs.map.proj.LarriveeProjection;
import com.jhlabs.map.proj.LaskowskiProjection;
import com.jhlabs.map.proj.LoximuthalProjection;
import com.jhlabs.map.proj.MBTFPPProjection;
import com.jhlabs.map.proj.MBTFPQProjection;
import com.jhlabs.map.proj.MBTFPSProjection;
import com.jhlabs.map.proj.MercatorProjection;
import com.jhlabs.map.proj.MillerProjection;
import com.jhlabs.map.proj.MolleweideProjection;
import com.jhlabs.map.proj.Murdoch1Projection;
import com.jhlabs.map.proj.Murdoch2Projection;
import com.jhlabs.map.proj.Murdoch3Projection;
import com.jhlabs.map.proj.NellProjection;
import com.jhlabs.map.proj.NicolosiProjection;
import com.jhlabs.map.proj.NullProjection;
import com.jhlabs.map.proj.ObliqueMercatorProjection;
import com.jhlabs.map.proj.OrthographicAzimuthalProjection;
import com.jhlabs.map.proj.PerspectiveConicProjection;
import com.jhlabs.map.proj.PerspectiveProjection;
import com.jhlabs.map.proj.PlateCarreeProjection;
import com.jhlabs.map.proj.PolyconicProjection;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;
import com.jhlabs.map.proj.PutninsP2Projection;
import com.jhlabs.map.proj.PutninsP4Projection;
import com.jhlabs.map.proj.PutninsP5PProjection;
import com.jhlabs.map.proj.PutninsP5Projection;
import com.jhlabs.map.proj.QuarticAuthalicProjection;
import com.jhlabs.map.proj.RectangularPolyconicProjection;
import com.jhlabs.map.proj.RobinsonProjection;
import com.jhlabs.map.proj.SinusoidalProjection;
import com.jhlabs.map.proj.StereographicAzimuthalProjection;
import com.jhlabs.map.proj.TCCProjection;
import com.jhlabs.map.proj.TCEAProjection;
import com.jhlabs.map.proj.TransverseMercatorProjection;
import com.jhlabs.map.proj.URMFPSProjection;
import com.jhlabs.map.proj.VanDerGrintenProjection;
import com.jhlabs.map.proj.VitkovskyProjection;
import com.jhlabs.map.proj.Wagner1Projection;
import com.jhlabs.map.proj.Wagner2Projection;
import com.jhlabs.map.proj.Wagner3Projection;
import com.jhlabs.map.proj.Wagner4Projection;
import com.jhlabs.map.proj.Wagner5Projection;
import com.jhlabs.map.proj.Wagner7Projection;
import com.jhlabs.map.proj.WerenskioldProjection;
import com.jhlabs.map.proj.WinkelTripelProjection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.text.ParsePosition;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class ProjectionFactory {
    private static final double SIXTH = 0.16666666666666666D;
    private static final double RA4 = 0.04722222222222222D;
    private static final double RA6 = 0.022156084656084655D;
    private static final double RV4 = 0.06944444444444445D;
    private static final double RV6 = 0.04243827160493827D;
    private static AngleFormat format = new AngleFormat("DdM", true);
    static Hashtable registry;

    public ProjectionFactory() {
    }

    public static Projection fromPROJ4Specification(String[] args) {
        Projection projection = null;
        Ellipsoid ellipsoid = null;
        double a = 0.0D;
        double b = 0.0D;
        double es = 0.0D;
        Hashtable params = new Hashtable();

        String ellipsoidName;
        for(int s = 0; s < args.length; ++s) {
            ellipsoidName = args[s];
            if(ellipsoidName.startsWith("+")) {
                int unit = ellipsoidName.indexOf(61);
                if(unit != -1) {
                    String i = ellipsoidName.substring(1, unit);
                    String value = ellipsoidName.substring(unit + 1);
                    params.put(i, value);
                }
            }
        }

        String var15 = (String)params.get("proj");
        if(var15 != null) {
            projection = getNamedPROJ4Projection(var15);
            if(projection == null) {
                throw new ProjectionException("Unknown projection: " + var15);
            }
        }

        var15 = (String)params.get("init");
        if(var15 != null) {
            projection = getNamedPROJ4CoordinateSystem(var15);
            if(projection == null) {
                throw new ProjectionException("Unknown projection: " + var15);
            }

            a = projection.getEquatorRadius();
            es = projection.getEllipsoid().getEccentricitySquared();
        }

        ellipsoidName = "";
        var15 = (String)params.get("R");
        if(var15 != null) {
            a = Double.parseDouble(var15);
        } else {
            var15 = (String)params.get("ellps");
            if(var15 == null) {
                var15 = (String)params.get("datum");
            }

            if(var15 == null) {
                var15 = (String)params.get("a");
                if(var15 != null) {
                    a = Double.parseDouble(var15);
                }

                var15 = (String)params.get("es");
                if(var15 != null) {
                    es = Double.parseDouble(var15);
                } else {
                    var15 = (String)params.get("rf");
                    if(var15 != null) {
                        es = Double.parseDouble(var15);
                        es *= 2.0D - es;
                    } else {
                        var15 = (String)params.get("f");
                        if(var15 != null) {
                            es = Double.parseDouble(var15);
                            es = 1.0D / es;
                            es *= 2.0D - es;
                        } else {
                            var15 = (String)params.get("b");
                            if(var15 != null) {
                                b = Double.parseDouble(var15);
                                es = 1.0D - b * b / (a * a);
                            }
                        }
                    }
                }

                if(b == 0.0D) {
                    b = a * Math.sqrt(1.0D - es);
                }
            } else {
                Ellipsoid[] var16 = Ellipsoid.ellipsoids;

                for(int var18 = 0; var18 < var16.length; ++var18) {
                    if(var16[var18].shortName.equals(var15)) {
                        ellipsoid = var16[var18];
                        break;
                    }
                }

                if(ellipsoid == null) {
                    throw new ProjectionException("Unknown ellipsoid: " + var15);
                }

                es = ellipsoid.eccentricity2;
                a = ellipsoid.equatorRadius;
                ellipsoidName = var15;
            }

            var15 = (String)params.get("R_A");
            if(var15 != null && Boolean.getBoolean(var15)) {
                a *= 1.0D - es * (0.16666666666666666D + es * (0.04722222222222222D + es * 0.022156084656084655D));
            } else {
                var15 = (String)params.get("R_V");
                if(var15 != null && Boolean.getBoolean(var15)) {
                    a *= 1.0D - es * (0.16666666666666666D + es * (0.06944444444444445D + es * 0.04243827160493827D));
                } else {
                    var15 = (String)params.get("R_a");
                    if(var15 != null && Boolean.getBoolean(var15)) {
                        a = 0.5D * (a + b);
                    } else {
                        var15 = (String)params.get("R_g");
                        if(var15 != null && Boolean.getBoolean(var15)) {
                            a = Math.sqrt(a * b);
                        } else {
                            var15 = (String)params.get("R_h");
                            if(var15 != null && Boolean.getBoolean(var15)) {
                                a = 2.0D * a * b / (a + b);
                                es = 0.0D;
                            } else {
                                var15 = (String)params.get("R_lat_a");
                                double var17;
                                if(var15 != null) {
                                    var17 = Math.sin(parseAngle(var15));
                                    if(Math.abs(var17) > 1.5707963267948966D) {
                                        throw new ProjectionException("-11");
                                    }

                                    var17 = 1.0D - es * var17 * var17;
                                    a *= 0.5D * (1.0D - es + var17) / (var17 * Math.sqrt(var17));
                                    es = 0.0D;
                                } else {
                                    var15 = (String)params.get("R_lat_g");
                                    if(var15 != null) {
                                        var17 = Math.sin(parseAngle(var15));
                                        if(Math.abs(var17) > 1.5707963267948966D) {
                                            throw new ProjectionException("-11");
                                        }

                                        var17 = 1.0D - es * var17 * var17;
                                        a *= Math.sqrt(1.0D - es) / var17;
                                        es = 0.0D;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        projection.setEllipsoid(new Ellipsoid(ellipsoidName, a, es, ellipsoidName));
        var15 = (String)params.get("lat_0");
        if(var15 != null) {
            projection.setProjectionLatitudeDegrees(parseAngle(var15));
        }

        var15 = (String)params.get("lon_0");
        if(var15 != null) {
            projection.setProjectionLongitudeDegrees(parseAngle(var15));
        }

        var15 = (String)params.get("lat_1");
        if(var15 != null) {
            projection.setProjectionLatitude1Degrees(parseAngle(var15));
        }

        var15 = (String)params.get("lat_2");
        if(var15 != null) {
            projection.setProjectionLatitude2Degrees(parseAngle(var15));
        }

        var15 = (String)params.get("lat_ts");
        if(var15 != null) {
            projection.setTrueScaleLatitudeDegrees(parseAngle(var15));
        }

        var15 = (String)params.get("x_0");
        if(var15 != null) {
            projection.setFalseEasting(Double.parseDouble(var15));
        }

        var15 = (String)params.get("y_0");
        if(var15 != null) {
            projection.setFalseNorthing(Double.parseDouble(var15));
        }

        var15 = (String)params.get("k_0");
        if(var15 == null) {
            var15 = (String)params.get("k");
        }

        if(var15 != null) {
            projection.setScaleFactor(Double.parseDouble(var15));
        }

        var15 = (String)params.get("units");
        if(var15 != null) {
            Unit var19 = Units.findUnits(var15);
            if(var19 != null) {
                projection.setFromMetres(1.0D / var19.value);
            }
        }

        var15 = (String)params.get("to_meter");
        if(var15 != null) {
            projection.setFromMetres(1.0D / Double.parseDouble(var15));
        }

        if(projection instanceof TransverseMercatorProjection) {
            var15 = (String)params.get("zone");
            if(var15 != null) {
                ((TransverseMercatorProjection)projection).setUTMZone(Integer.parseInt(var15));
            }
        }

        projection.initialize();
        return projection;
    }

    private static double parseAngle(String s) {
        return format.parse(s, (ParsePosition)null).doubleValue();
    }

    static void register(String name, Class cls, String description) {
        registry.put(name, cls);
    }

    static Projection getNamedPROJ4Projection(String name) {
        if(registry == null) {
            initialize();
        }

        Class cls = (Class)registry.get(name);
        if(cls != null) {
            try {
                Projection e = (Projection)cls.newInstance();
                if(e != null) {
                    e.setName(name);
                }

                return e;
            } catch (IllegalAccessException var3) {
                var3.printStackTrace();
            } catch (InstantiationException var4) {
                var4.printStackTrace();
            }
        }

        return null;
    }

    static void initialize() {
        registry = new Hashtable();
        register("aea", AlbersProjection.class, "Albers Equal Area");
        register("aeqd", EquidistantAzimuthalProjection.class, "Azimuthal Equidistant");
        register("airy", AiryProjection.class, "Airy");
        register("aitoff", AitoffProjection.class, "Aitoff");
        register("alsk", Projection.class, "Mod. Stereographics of Alaska");
        register("apian", Projection.class, "Apian Globular I");
        register("august", AugustProjection.class, "August Epicycloidal");
        register("bacon", Projection.class, "Bacon Globular");
        register("bipc", BipolarProjection.class, "Bipolar conic of western hemisphere");
        register("boggs", BoggsProjection.class, "Boggs Eumorphic");
        register("bonne", BonneProjection.class, "Bonne (Werner lat_1=90)");
        register("cass", CassiniProjection.class, "Cassini");
        register("cc", CentralCylindricalProjection.class, "Central Cylindrical");
        register("cea", Projection.class, "Equal Area Cylindrical");
        register("collg", CollignonProjection.class, "Collignon");
        register("crast", CrasterProjection.class, "Craster Parabolic (Putnins P4)");
        register("denoy", DenoyerProjection.class, "Denoyer Semi-Elliptical");
        register("eck1", Eckert1Projection.class, "Eckert I");
        register("eck2", Eckert2Projection.class, "Eckert II");
        register("eck4", Eckert4Projection.class, "Eckert IV");
        register("eck5", Eckert5Projection.class, "Eckert V");
        register("eqc", PlateCarreeProjection.class, "Equidistant Cylindrical (Plate Caree)");
        register("eqdc", EquidistantConicProjection.class, "Equidistant Conic");
        register("euler", EulerProjection.class, "Euler");
        register("fahey", FaheyProjection.class, "Fahey");
        register("fouc", FoucautProjection.class, "Foucaut");
        register("fouc_s", FoucautSinusoidalProjection.class, "Foucaut Sinusoidal");
        register("gall", GallProjection.class, "Gall (Gall Stereographic)");
        register("gnom", GnomonicAzimuthalProjection.class, "Gnomonic");
        register("goode", GoodeProjection.class, "Goode Homolosine");
        register("hammer", HammerProjection.class, "Hammer & Eckert-Greifendorff");
        register("hatano", HatanoProjection.class, "Hatano Asymmetrical Equal Area");
        register("kav5", KavraiskyVProjection.class, "Kavraisky V");
        register("lagrng", LagrangeProjection.class, "Lagrange");
        register("larr", LarriveeProjection.class, "Larrivee");
        register("lask", LaskowskiProjection.class, "Laskowski");
        register("latlong", NullProjection.class, "Lat/Long");
        register("lcc", LambertConformalConicProjection.class, "Lambert Conformal Conic");
        register("leac", LambertEqualAreaConicProjection.class, "Lambert Equal Area Conic");
        register("loxim", LoximuthalProjection.class, "Loximuthal");
        register("lsat", LandsatProjection.class, "Space oblique for LANDSAT");
        register("mbt_fps", MBTFPSProjection.class, "McBryde-Thomas Flat-Pole Sine (No. 2)");
        register("mbtfpp", MBTFPPProjection.class, "McBride-Thomas Flat-Polar Parabolic");
        register("mbtfpq", MBTFPQProjection.class, "McBryde-Thomas Flat-Polar Quartic");
        register("merc", MercatorProjection.class, "Mercator");
        register("mill", MillerProjection.class, "Miller Cylindrical");
        register("moll", MolleweideProjection.class, "Mollweide");
        register("murd1", Murdoch1Projection.class, "Murdoch I");
        register("murd2", Murdoch2Projection.class, "Murdoch II");
        register("murd3", Murdoch3Projection.class, "Murdoch III");
        register("nell", NellProjection.class, "Nell");
        register("nicol", NicolosiProjection.class, "Nicolosi Globular");
        register("nsper", PerspectiveProjection.class, "Near-sided perspective");
        register("omerc", ObliqueMercatorProjection.class, "Oblique Mercator");
        register("ortho", OrthographicAzimuthalProjection.class, "Orthographic");
        register("pconic", PerspectiveConicProjection.class, "Perspective Conic");
        register("poly", PolyconicProjection.class, "Polyconic (American)");
        register("putp2", PutninsP2Projection.class, "Putnins P2");
        register("putp4p", PutninsP4Projection.class, "Putnins P4\'");
        register("putp5", PutninsP5Projection.class, "Putnins P5");
        register("putp5p", PutninsP5PProjection.class, "Putnins P5\'");
        register("qua_aut", QuarticAuthalicProjection.class, "Quartic Authalic");
        register("robin", RobinsonProjection.class, "Robinson");
        register("rpoly", RectangularPolyconicProjection.class, "Rectangular Polyconic");
        register("sinu", SinusoidalProjection.class, "Sinusoidal (Sanson-Flamsteed)");
        register("stere", StereographicAzimuthalProjection.class, "Stereographic");
        register("tcc", TCCProjection.class, "Transverse Central Cylindrical");
        register("tcea", TCEAProjection.class, "Transverse Cylindrical Equal Area");
        register("tmerc", TransverseMercatorProjection.class, "Transverse Mercator");
        register("urmfps", URMFPSProjection.class, "Urmaev Flat-Polar Sinusoidal");
        register("utm", TransverseMercatorProjection.class, "Universal Transverse Mercator (UTM)");
        register("vandg", VanDerGrintenProjection.class, "van der Grinten (I)");
        register("vitk1", VitkovskyProjection.class, "Vitkovsky I");
        register("wag1", Wagner1Projection.class, "Wagner I (Kavraisky VI)");
        register("wag2", Wagner2Projection.class, "Wagner II");
        register("wag3", Wagner3Projection.class, "Wagner III");
        register("wag4", Wagner4Projection.class, "Wagner IV");
        register("wag5", Wagner5Projection.class, "Wagner V");
        register("wag7", Wagner7Projection.class, "Wagner VII");
        register("weren", WerenskioldProjection.class, "Werenskiold I");
        register("wintri", WinkelTripelProjection.class, "Winkel Tripel");
    }

    public static Projection readProjectionFile(String file, String name) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ProjectionFactory.class.getResourceAsStream("/nad/" + file)));
        StreamTokenizer t = new StreamTokenizer(reader);
        t.commentChar(35);
        t.ordinaryChars(48, 57);
        t.ordinaryChars(46, 46);
        t.ordinaryChars(45, 45);
        t.ordinaryChars(43, 43);
        t.wordChars(48, 57);
        t.wordChars(39, 39);
        t.wordChars(34, 34);
        t.wordChars(95, 95);
        t.wordChars(46, 46);
        t.wordChars(45, 45);
        t.wordChars(43, 43);
        t.wordChars(44, 44);
        t.nextToken();

        String cname;
        Vector v;
        do {
            if(t.ttype != 60) {
                reader.close();
                return null;
            }

            t.nextToken();
            if(t.ttype != -3) {
                throw new IOException(t.lineno() + ": Word expected after \'<\'");
            }

            cname = t.sval;
            t.nextToken();
            if(t.ttype != 62) {
                throw new IOException(t.lineno() + ": \'>\' expected");
            }

            t.nextToken();
            v = new Vector();
            String values = "";

            while(t.ttype != 60) {
                if(t.ttype == 43) {
                    t.nextToken();
                }

                if(t.ttype != -3) {
                    throw new IOException(t.lineno() + ": Word expected after \'+\'");
                }

                String args = t.sval;
                t.nextToken();
                if(t.ttype == 61) {
                    t.nextToken();
                    String value = t.sval;
                    t.nextToken();
                    if(args.startsWith("+")) {
                        v.add(args + "=" + value);
                    } else {
                        v.add("+" + args + "=" + value);
                    }
                }
            }

            t.nextToken();
            if(t.ttype != 62) {
                throw new IOException(t.lineno() + ": \'<>\' expected");
            }

            t.nextToken();
        } while(!cname.equals(name));

        String[] args1 = new String[v.size()];
        v.copyInto(args1);
        reader.close();
        return fromPROJ4Specification(args1);
    }

    public static Projection getNamedPROJ4CoordinateSystem(String name) {
        String[] files = new String[]{"world", "nad83", "nad27", "esri", "epsg"};

        try {
            int e = name.indexOf(58);
            if(e >= 0) {
                return readProjectionFile(name.substring(0, e), name.substring(e + 1));
            }

            for(int i = 0; i < files.length; ++i) {
                Projection projection = readProjectionFile(files[i], name);
                if(projection != null) {
                    return projection;
                }
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        Projection projection = fromPROJ4Specification(args);
        if(projection != null) {
            System.out.println(projection.getPROJ4Description());

            for(int i = 0; i < args.length; ++i) {
                String arg = args[i];
                if(!arg.startsWith("+") && !arg.startsWith("-")) {
                    try {
                        BufferedReader e = new BufferedReader(new FileReader(new File(args[i])));
                        com.jhlabs.map.Point2D.Double p = new com.jhlabs.map.Point2D.Double();

                        String line;
                        while((line = e.readLine()) != null) {
                            StringTokenizer t = new StringTokenizer(line, " ");
                            String slon = t.nextToken();
                            String slat = t.nextToken();
                            p.x = format.parse(slon, (ParsePosition)null).doubleValue();
                            p.y = format.parse(slat, (ParsePosition)null).doubleValue();
                            projection.transform(p, p);
                            System.out.println(p.x + " " + p.y);
                        }
                    } catch (IOException var10) {
                        System.out.println("IOException: " + args[i] + ": " + var10.getMessage());
                    }
                }
            }
        } else {
            System.out.println("Can\'t find projection " + args[0]);
        }

    }
}
