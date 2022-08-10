package br.com.infox.epp.access.util.quartzo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class Codecs {
    private static byte[] Base64EncMap;
    private static byte[] Base64DecMap;
    static BitSet dontNeedEncoding;
    static final int caseDiff = 32;
    static String dfltEncName;

    private Codecs() {
    }

    public static final String base64Encode(String var0) {
        return base64Encode(var0, "8859_1");
    }

    public static final String base64Encode(String var0, String var1) {
        if (var0 == null) {
            return null;
        } else {
            try {
                return new String(base64Encode(var0.getBytes(var1)), var1);
            } catch (UnsupportedEncodingException var3) {
                throw new Error(var3.toString());
            }
        }
    }

    public static final byte[] base64Encode(byte[] var0) {
        if (var0 == null) {
            return null;
        } else {
            byte[] var3 = new byte[(var0.length + 2) / 3 * 4];
            int var1 = 0;

            int var2;
            for(var2 = 0; var1 < var0.length - 2; var1 += 3) {
                var3[var2++] = Base64EncMap[var0[var1] >>> 2 & 63];
                var3[var2++] = Base64EncMap[var0[var1 + 1] >>> 4 & 15 | var0[var1] << 4 & 63];
                var3[var2++] = Base64EncMap[var0[var1 + 2] >>> 6 & 3 | var0[var1 + 1] << 2 & 63];
                var3[var2++] = Base64EncMap[var0[var1 + 2] & 63];
            }

            if (var1 < var0.length) {
                var3[var2++] = Base64EncMap[var0[var1] >>> 2 & 63];
                if (var1 < var0.length - 1) {
                    var3[var2++] = Base64EncMap[var0[var1 + 1] >>> 4 & 15 | var0[var1] << 4 & 63];
                    var3[var2++] = Base64EncMap[var0[var1 + 1] << 2 & 63];
                } else {
                    var3[var2++] = Base64EncMap[var0[var1] << 4 & 63];
                }
            }

            while(var2 < var3.length) {
                var3[var2] = 61;
                ++var2;
            }

            return var3;
        }
    }

    public static final String base64Decode(String var0, String var1) {
        if (var0 == null) {
            return null;
        } else {
            try {
                return new String(base64Decode(var0.getBytes(var1)), var1);
            } catch (UnsupportedEncodingException var3) {
                throw new Error(var3.toString());
            }
        }
    }

    public static final String base64Decode(String var0) {
        return base64Decode(var0, "8859_1");
    }

    public static final byte[] base64Decode(byte[] var0) {
        if (var0 == null) {
            return null;
        } else {
            int var1;
            for(var1 = var0.length; var0[var1 - 1] == 61; --var1) {
            }

            byte[] var2 = new byte[var1 - var0.length / 4];

            int var3;
            for(var3 = 0; var3 < var0.length; ++var3) {
                var0[var3] = Base64DecMap[var0[var3]];
            }

            try {
                var3 = 0;

                int var4;
                for(var4 = 0; var4 < var2.length - 2; var4 += 3) {
                    var2[var4] = (byte)(var0[var3] << 2 & 255 | var0[var3 + 1] >>> 4 & 3);
                    var2[var4 + 1] = (byte)(var0[var3 + 1] << 4 & 255 | var0[var3 + 2] >>> 2 & 15);
                    var2[var4 + 2] = (byte)(var0[var3 + 2] << 6 & 255 | var0[var3 + 3] & 63);
                    var3 += 4;
                }

                if (var4 < var2.length) {
                    var2[var4] = (byte)(var0[var3] << 2 & 255 | var0[var3 + 1] >>> 4 & 3);
                }

                ++var4;
                if (var4 < var2.length) {
                    var2[var4] = (byte)(var0[var3 + 1] << 4 & 255 | var0[var3 + 2] >>> 2 & 15);
                }
            } catch (ArrayIndexOutOfBoundsException var5) {
            }

            return var2;
        }
    }

    public static final String URLDecode(String var0) {
        if (var0 == null) {
            return null;
        } else {
            char[] var1 = new char[var0.length()];
            int var2 = 0;

            for(int var3 = 0; var3 < var0.length(); ++var3) {
                char var4 = var0.charAt(var3);
                if (var4 == '+') {
                    var1[var2++] = ' ';
                } else if (var4 == '%') {
                    try {
                        var1[var2++] = (char)Integer.parseInt(var0.substring(var3 + 1, var3 + 3), 16);
                        var3 += 2;
                    } catch (NumberFormatException var6) {
                        System.err.println(var0.substring(var3, var3 + 3) + " is an invalid code");
                    }
                } else {
                    var1[var2++] = var4;
                }
            }

            return String.valueOf(var1, 0, var2);
        }
    }

    /** @deprecated */
    public static String encode(String var0) {
        String var1 = null;

        try {
            var1 = encode(var0, dfltEncName);
        } catch (UnsupportedEncodingException var3) {
        }

        return var1;
    }

    public static String encode(String var0, String var1) throws UnsupportedEncodingException {
        boolean var2 = false;
        boolean var3 = false;
        byte var4 = 10;
        StringBuffer var5 = new StringBuffer(var0.length());
        ByteArrayOutputStream var6 = new ByteArrayOutputStream(var4);
        OutputStreamWriter var7 = new OutputStreamWriter(var6, var1);

        for(int var8 = 0; var8 < var0.length(); ++var8) {
            char var9 = var0.charAt(var8);
            if (dontNeedEncoding.get(var9)) {
                if (var9 == ' ') {
                    var9 = '+';
                    var2 = true;
                }

                var5.append((char)var9);
                var3 = true;
            } else {
                try {
                    if (var3) {
                        var7 = new OutputStreamWriter(var6, var1);
                        var3 = false;
                    }

                    var7.write(var9);
                    if (var9 >= '\ud800' && var9 <= '\udbff' && var8 + 1 < var0.length()) {
                        char var10 = var0.charAt(var8 + 1);
                        if (var10 >= '\udc00' && var10 <= '\udfff') {
                            var7.write(var10);
                            ++var8;
                        }
                    }

                    var7.flush();
                } catch (IOException var13) {
                    var6.reset();
                    continue;
                }

                byte[] var14 = var6.toByteArray();

                for(int var11 = 0; var11 < var14.length; ++var11) {
                    var5.append('%');
                    char var12 = Character.forDigit(var14[var11] >> 4 & 15, 16);
                    if (Character.isLetter(var12)) {
                        var12 = (char)(var12 - 32);
                    }

                    var5.append(var12);
                    var12 = Character.forDigit(var14[var11] & 15, 16);
                    if (Character.isLetter(var12)) {
                        var12 = (char)(var12 - 32);
                    }

                    var5.append(var12);
                }

                var6.reset();
                var2 = true;
            }
        }

        return var2 ? var5.toString() : var0;
    }

    /** @deprecated */
    public static String decode(String var0) {
        String var1 = null;

        try {
            var1 = decode(var0, dfltEncName);
        } catch (UnsupportedEncodingException var3) {
        }

        return var1;
    }

    public static String decode(String var0, String var1) throws UnsupportedEncodingException {
        boolean var2 = false;
        StringBuffer var3 = new StringBuffer();
        int var4 = var0.length();
        int var5 = 0;
        if (var1.length() == 0) {
            throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
        } else {
            while(true) {
                while(var5 < var4) {
                    char var6 = var0.charAt(var5);
                    switch (var6) {
                        case '%':
                            try {
                                byte[] var7 = new byte[(var4 - var5) / 3];
                                int var8 = 0;

                                while(var5 + 2 < var4 && var6 == '%') {
                                    var7[var8++] = (byte)Integer.parseInt(var0.substring(var5 + 1, var5 + 3), 16);
                                    var5 += 3;
                                    if (var5 < var4) {
                                        var6 = var0.charAt(var5);
                                    }
                                }

                                if (var5 < var4 && var6 == '%') {
                                    throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                                }

                                var3.append(new String(var7, 0, var8, var1));
                            } catch (NumberFormatException var9) {
                                throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + var9.getMessage());
                            }

                            var2 = true;
                            break;
                        case '+':
                            var3.append(' ');
                            ++var5;
                            var2 = true;
                            break;
                        default:
                            var3.append(var6);
                            ++var5;
                    }
                }

                return var2 ? var3.toString() : var0;
            }
        }
    }

    static {
        byte[] var0 = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        Base64EncMap = var0;
        Base64DecMap = new byte[128];

        for(int var1 = 0; var1 < Base64EncMap.length; ++var1) {
            Base64DecMap[Base64EncMap[var1]] = (byte)var1;
        }

        dfltEncName = null;
        dontNeedEncoding = new BitSet(256);

        int var2;
        for(var2 = 97; var2 <= 122; ++var2) {
            dontNeedEncoding.set(var2);
        }

        for(var2 = 65; var2 <= 90; ++var2) {
            dontNeedEncoding.set(var2);
        }

        for(var2 = 48; var2 <= 57; ++var2) {
            dontNeedEncoding.set(var2);
        }

        dontNeedEncoding.set(32);
        dontNeedEncoding.set(45);
        dontNeedEncoding.set(95);
        dontNeedEncoding.set(46);
        dontNeedEncoding.set(42);
        dfltEncName = "UTF8";
    }
}
