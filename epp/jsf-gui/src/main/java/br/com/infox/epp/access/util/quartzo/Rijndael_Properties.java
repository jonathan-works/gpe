package br.com.infox.epp.access.util.quartzo;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

public class Rijndael_Properties {
    static final boolean GLOBAL_DEBUG = false;
    static final String ALGORITHM = "Rijndael";
    static final double VERSION = 0.1;
    static final String FULL_NAME = "Rijndael ver. 0.1";
    static final String NAME = "Rijndael_Properties";
    static final Properties properties = new Properties();
    private static final String[][] DEFAULT_PROPERTIES = new String[][]{{"Trace.Rijndael_Algorithm", "true"}, {"Debug.Level.*", "1"}, {"Debug.Level.Rijndael_Algorithm", "9"}};

    public Rijndael_Properties() {
    }

    public static String getProperty(String var0) {
        return properties.getProperty(var0);
    }

    public static String getProperty(String var0, String var1) {
        return properties.getProperty(var0, var1);
    }

    public static void list(PrintStream var0) {
        list(new PrintWriter(var0, true));
    }

    public static void list(PrintWriter var0) {
        var0.println("#");
        var0.println("# ----- Begin Rijndael properties -----");
        var0.println("#");
        Enumeration var3 = properties.propertyNames();

        while(var3.hasMoreElements()) {
            String var1 = (String)var3.nextElement();
            String var2 = getProperty(var1);
            var0.println(var1 + " = " + var2);
        }

        var0.println("#");
        var0.println("# ----- End Rijndael properties -----");
    }

    public static Enumeration propertyNames() {
        return properties.propertyNames();
    }

    static boolean isTraceable(String var0) {
        String var1 = getProperty("Trace." + var0);
        return var1 == null ? false : new Boolean(var1);
    }

    static int getLevel(String var0) {
        String var1 = getProperty("Debug.Level." + var0);
        if (var1 == null) {
            var1 = getProperty("Debug.Level.*");
            if (var1 == null) {
                return 0;
            }
        }

        try {
            return Integer.parseInt(var1);
        } catch (NumberFormatException var3) {
            return 0;
        }
    }

    static PrintWriter getOutput() {
        String var1 = getProperty("Output");
        PrintWriter var0;
        if (var1 != null && var1.equals("out")) {
            var0 = new PrintWriter(System.out, true);
        } else {
            var0 = new PrintWriter(System.err, true);
        }

        return var0;
    }

    static {
        String var0 = "Rijndael.properties";
        InputStream var1 = Rijndael_Properties.class.getResourceAsStream(var0);
        boolean var2 = var1 != null;
        if (var2) {
            try {
                properties.load(var1);
                var1.close();
            } catch (Exception var5) {
                var2 = false;
            }
        }

        if (!var2) {
            int var3 = DEFAULT_PROPERTIES.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                properties.put(DEFAULT_PROPERTIES[var4][0], DEFAULT_PROPERTIES[var4][1]);
            }
        }

    }
}
