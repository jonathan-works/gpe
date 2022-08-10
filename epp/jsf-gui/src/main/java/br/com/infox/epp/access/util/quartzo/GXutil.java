package br.com.infox.epp.access.util.quartzo;

public class GXutil {

    public static String rtrim(String var0) {
        if (var0 == null) {
            return "";
        } else {
            int var1 = 0;

            int var2;
            for(var2 = var0.length() - 1; var2 >= 0; --var2) {
                if (var0.charAt(var2) != ' ') {
                    var1 = var2;
                    break;
                }
            }

            return var2 < 0 ? "" : var0.substring(0, var1 + 1);
        }
    }

    public static String padl(String var0, int var1, String var2) {
        String var3 = new String(ltrim(var0));
        int var4 = var3.length();
        if (var1 > var4) {
            int var5 = (var1 - var4) / var2.length() + 1;
            String var6 = left(replicate(var2, var5), var1 - var4);
            return left(var6 + var3, var1);
        } else {
            return left(var3, var1);
        }
    }

    public static String ltrim(String var0) {
        int var1;
        for(var1 = 0; var1 < var0.length() && var0.charAt(var1) == ' '; ++var1) {
        }

        return var0.substring(var1, var0.length());
    }

    public static String left(String var0, int var1) {
        if (var0 == null) {
            return "";
        } else {
            int var2 = var0.length();
            return var2 >= var1 && var1 >= 0 ? var0.substring(0, var1 < var2 ? var1 : var2) : var0;
        }
    }

    public static String replicate(char var0, int var1) {
        if (var1 <= 0) {
            return "";
        } else {
            StringBuffer var2 = new StringBuffer(var1);

            for(int var3 = 0; var3 < var1; ++var3) {
                var2.append(var0);
            }

            return var2.toString();
        }
    }

    public static String replicate(String var0, int var1, int var2) {
        if (var1 <= 0) {
            return "";
        } else {
            StringBuffer var3 = new StringBuffer(var1);

            for(int var4 = 0; var4 < var1; ++var4) {
                var3.append(var0);
            }

            return var3.toString();
        }
    }

    public static final String replicate(String var0, int var1) {
        if (var1 <= 0) {
            return "";
        } else if (var0.length() == 0) {
            return "";
        } else {
            char var2 = var0.charAt(0);
            char[] var3 = new char[var1];

            for(int var4 = 0; var4 < var1; ++var4) {
                var3[var4] = var2;
            }

            return new String(var3);
        }
    }
    public static String upper(String var0) {
        int var1 = var0.length();
        int var2 = -1;

        int var4;
        for(int var3 = var1 - 1; var3 >= 0; --var3) {
            var4 = var0.charAt(var3);
            if (Character.toUpperCase((char)var4) != var4) {
                var2 = var3;
                break;
            }
        }

        if (var2 == -1) {
            return var0;
        } else {
            char[] var5 = new char[var1];
            var0.getChars(0, var1, var5, 0);

            for(var4 = var2; var4 >= 0; --var4) {
                var5[var4] = Character.toUpperCase(var5[var4]);
            }

            return new String(var5);
        }
    }

}
