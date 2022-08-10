package br.com.infox.epp.access.util.quartzo;

import java.io.PrintWriter;
import java.security.InvalidKeyException;

public final class Rijndael_Algorithm {
    static final String NAME = "Rijndael_Algorithm";
    static final boolean IN = true;
    static final boolean OUT = false;
    static final boolean RDEBUG = false;
    static final int debuglevel = 0;
    static final PrintWriter err = null;
    static final boolean TRACE = Rijndael_Properties.isTraceable("Rijndael_Algorithm");
    public static final int BLOCK_SIZE = 16;
    static final int[] alog = new int[256];
    static final int[] log = new int[256];
    static final byte[] S = new byte[256];
    static final byte[] Si = new byte[256];
    static final int[] T1 = new int[256];
    static final int[] T2 = new int[256];
    static final int[] T3 = new int[256];
    static final int[] T4 = new int[256];
    static final int[] T5 = new int[256];
    static final int[] T6 = new int[256];
    static final int[] T7 = new int[256];
    static final int[] T8 = new int[256];
    static final int[] U1 = new int[256];
    static final int[] U2 = new int[256];
    static final int[] U3 = new int[256];
    static final int[] U4 = new int[256];
    static final byte[] rcon = new byte[30];
    static final int[][][] shifts = new int[][][]{{{0, 0}, {1, 3}, {2, 2}, {3, 1}}, {{0, 0}, {1, 5}, {2, 4}, {3, 3}}, {{0, 0}, {1, 7}, {3, 5}, {4, 4}}};
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public Rijndael_Algorithm() {
    }

    static void debug(String var0) {
        err.println(">>> Rijndael_Algorithm: " + var0);
    }

    static void trace(boolean var0, String var1) {
        if (TRACE) {
            err.println((var0 ? "==> " : "<== ") + "Rijndael_Algorithm" + "." + var1);
        }

    }

    static void trace(String var0) {
        if (TRACE) {
            err.println("<=> Rijndael_Algorithm." + var0);
        }

    }

    static final int mul(int var0, int var1) {
        return var0 != 0 && var1 != 0 ? alog[(log[var0 & 255] + log[var1 & 255]) % 255] : 0;
    }

    static final int mul4(int var0, byte[] var1) {
        if (var0 == 0) {
            return 0;
        } else {
            var0 = log[var0 & 255];
            int var2 = var1[0] != 0 ? alog[(var0 + log[var1[0] & 255]) % 255] & 255 : 0;
            int var3 = var1[1] != 0 ? alog[(var0 + log[var1[1] & 255]) % 255] & 255 : 0;
            int var4 = var1[2] != 0 ? alog[(var0 + log[var1[2] & 255]) % 255] & 255 : 0;
            int var5 = var1[3] != 0 ? alog[(var0 + log[var1[3] & 255]) % 255] & 255 : 0;
            return var2 << 24 | var3 << 16 | var4 << 8 | var5;
        }
    }

    public static final Object makeKey(byte[] var0) throws InvalidKeyException {
        return makeKey(var0, 16);
    }

    public static final void blockEncrypt(byte[] var0, byte[] var1, int var2, Object var3) {
        int[][] var4 = (int[][])((int[][])((Object[])((Object[])var3))[0]);
        int var5 = var4.length - 1;
        int[] var6 = var4[0];
        int var7 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[0];
        int var8 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[1];
        int var9 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[2];
        int var10 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[3];

        int var15;
        for(var15 = 1; var15 < var5; ++var15) {
            var6 = var4[var15];
            int var11 = T1[var7 >>> 24 & 255] ^ T2[var8 >>> 16 & 255] ^ T3[var9 >>> 8 & 255] ^ T4[var10 & 255] ^ var6[0];
            int var12 = T1[var8 >>> 24 & 255] ^ T2[var9 >>> 16 & 255] ^ T3[var10 >>> 8 & 255] ^ T4[var7 & 255] ^ var6[1];
            int var13 = T1[var9 >>> 24 & 255] ^ T2[var10 >>> 16 & 255] ^ T3[var7 >>> 8 & 255] ^ T4[var8 & 255] ^ var6[2];
            int var14 = T1[var10 >>> 24 & 255] ^ T2[var7 >>> 16 & 255] ^ T3[var8 >>> 8 & 255] ^ T4[var9 & 255] ^ var6[3];
            var7 = var11;
            var8 = var12;
            var9 = var13;
            var10 = var14;
        }

        var6 = var4[var5];
        var15 = var6[0];
        var1[0] = (byte)(S[var7 >>> 24 & 255] ^ var15 >>> 24);
        var1[1] = (byte)(S[var8 >>> 16 & 255] ^ var15 >>> 16);
        var1[2] = (byte)(S[var9 >>> 8 & 255] ^ var15 >>> 8);
        var1[3] = (byte)(S[var10 & 255] ^ var15);
        var15 = var6[1];
        var1[4] = (byte)(S[var8 >>> 24 & 255] ^ var15 >>> 24);
        var1[5] = (byte)(S[var9 >>> 16 & 255] ^ var15 >>> 16);
        var1[6] = (byte)(S[var10 >>> 8 & 255] ^ var15 >>> 8);
        var1[7] = (byte)(S[var7 & 255] ^ var15);
        var15 = var6[2];
        var1[8] = (byte)(S[var9 >>> 24 & 255] ^ var15 >>> 24);
        var1[9] = (byte)(S[var10 >>> 16 & 255] ^ var15 >>> 16);
        var1[10] = (byte)(S[var7 >>> 8 & 255] ^ var15 >>> 8);
        var1[11] = (byte)(S[var8 & 255] ^ var15);
        var15 = var6[3];
        var1[12] = (byte)(S[var10 >>> 24 & 255] ^ var15 >>> 24);
        var1[13] = (byte)(S[var7 >>> 16 & 255] ^ var15 >>> 16);
        var1[14] = (byte)(S[var8 >>> 8 & 255] ^ var15 >>> 8);
        var1[15] = (byte)(S[var9 & 255] ^ var15);
    }

    public static final void blockDecrypt(byte[] var0, byte[] var1, int var2, Object var3) {
        int[][] var4 = (int[][])((int[][])((Object[])((Object[])var3))[1]);
        int var5 = var4.length - 1;
        int[] var6 = var4[0];
        int var7 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[0];
        int var8 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[1];
        int var9 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[2];
        int var10 = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[3];

        int var15;
        for(var15 = 1; var15 < var5; ++var15) {
            var6 = var4[var15];
            int var11 = T5[var7 >>> 24 & 255] ^ T6[var10 >>> 16 & 255] ^ T7[var9 >>> 8 & 255] ^ T8[var8 & 255] ^ var6[0];
            int var12 = T5[var8 >>> 24 & 255] ^ T6[var7 >>> 16 & 255] ^ T7[var10 >>> 8 & 255] ^ T8[var9 & 255] ^ var6[1];
            int var13 = T5[var9 >>> 24 & 255] ^ T6[var8 >>> 16 & 255] ^ T7[var7 >>> 8 & 255] ^ T8[var10 & 255] ^ var6[2];
            int var14 = T5[var10 >>> 24 & 255] ^ T6[var9 >>> 16 & 255] ^ T7[var8 >>> 8 & 255] ^ T8[var7 & 255] ^ var6[3];
            var7 = var11;
            var8 = var12;
            var9 = var13;
            var10 = var14;
        }

        var6 = var4[var5];
        var15 = var6[0];
        var1[0] = (byte)(Si[var7 >>> 24 & 255] ^ var15 >>> 24);
        var1[1] = (byte)(Si[var10 >>> 16 & 255] ^ var15 >>> 16);
        var1[2] = (byte)(Si[var9 >>> 8 & 255] ^ var15 >>> 8);
        var1[3] = (byte)(Si[var8 & 255] ^ var15);
        var15 = var6[1];
        var1[4] = (byte)(Si[var8 >>> 24 & 255] ^ var15 >>> 24);
        var1[5] = (byte)(Si[var7 >>> 16 & 255] ^ var15 >>> 16);
        var1[6] = (byte)(Si[var10 >>> 8 & 255] ^ var15 >>> 8);
        var1[7] = (byte)(Si[var9 & 255] ^ var15);
        var15 = var6[2];
        var1[8] = (byte)(Si[var9 >>> 24 & 255] ^ var15 >>> 24);
        var1[9] = (byte)(Si[var8 >>> 16 & 255] ^ var15 >>> 16);
        var1[10] = (byte)(Si[var7 >>> 8 & 255] ^ var15 >>> 8);
        var1[11] = (byte)(Si[var10 & 255] ^ var15);
        var15 = var6[3];
        var1[12] = (byte)(Si[var10 >>> 24 & 255] ^ var15 >>> 24);
        var1[13] = (byte)(Si[var9 >>> 16 & 255] ^ var15 >>> 16);
        var1[14] = (byte)(Si[var8 >>> 8 & 255] ^ var15 >>> 8);
        var1[15] = (byte)(Si[var7 & 255] ^ var15);
    }

    public static boolean self_test() {
        return self_test(16);
    }

    public static final int blockSize() {
        return 16;
    }

    public static final synchronized Object makeKey(byte[] var0, int var1) throws InvalidKeyException {
        if (var0 == null) {
            throw new InvalidKeyException("Empty key");
        } else if (var0.length != 16 && var0.length != 24 && var0.length != 32) {
            throw new InvalidKeyException("Incorrect key length");
        } else {
            int var2 = getRounds(var0.length, var1);
            int var3 = var1 / 4;
            int[][] var4 = new int[var2 + 1][var3];
            int[][] var5 = new int[var2 + 1][var3];
            int var6 = (var2 + 1) * var3;
            int var7 = var0.length / 4;
            int[] var8 = new int[var7];
            int var9 = 0;

            int var10;
            for(var10 = 0; var9 < var7; var8[var9++] = (var0[var10++] & 255) << 24 | (var0[var10++] & 255) << 16 | (var0[var10++] & 255) << 8 | var0[var10++] & 255) {
            }

            int var11 = 0;

            for(var10 = 0; var10 < var7 && var11 < var6; ++var11) {
                var4[var11 / var3][var11 % var3] = var8[var10];
                var5[var2 - var11 / var3][var11 % var3] = var8[var10];
                ++var10;
            }

            int var13 = 0;

            int var12;
            while(var11 < var6) {
                var12 = var8[var7 - 1];
                var8[0] ^= (S[var12 >>> 16 & 255] & 255) << 24 ^ (S[var12 >>> 8 & 255] & 255) << 16 ^ (S[var12 & 255] & 255) << 8 ^ S[var12 >>> 24 & 255] & 255 ^ (rcon[var13++] & 255) << 24;
                if (var7 != 8) {
                    var9 = 1;

                    for(var10 = 0; var9 < var7; ++var9) {
                        var8[var9] ^= var8[var10++];
                    }
                } else {
                    var9 = 1;

                    for(var10 = 0; var9 < var7 / 2; ++var9) {
                        var8[var9] ^= var8[var10++];
                    }

                    var12 = var8[var7 / 2 - 1];
                    var8[var7 / 2] ^= S[var12 & 255] & 255 ^ (S[var12 >>> 8 & 255] & 255) << 8 ^ (S[var12 >>> 16 & 255] & 255) << 16 ^ (S[var12 >>> 24 & 255] & 255) << 24;
                    var10 = var7 / 2;

                    for(var9 = var10 + 1; var9 < var7; ++var9) {
                        var8[var9] ^= var8[var10++];
                    }
                }

                for(var10 = 0; var10 < var7 && var11 < var6; ++var11) {
                    var4[var11 / var3][var11 % var3] = var8[var10];
                    var5[var2 - var11 / var3][var11 % var3] = var8[var10];
                    ++var10;
                }
            }

            for(int var14 = 1; var14 < var2; ++var14) {
                for(var10 = 0; var10 < var3; ++var10) {
                    var12 = var5[var14][var10];
                    var5[var14][var10] = U1[var12 >>> 24 & 255] ^ U2[var12 >>> 16 & 255] ^ U3[var12 >>> 8 & 255] ^ U4[var12 & 255];
                }
            }

            Object[] var15 = new Object[]{var4, var5};
            return var15;
        }
    }

    public static final void blockEncrypt(byte[] var0, byte[] var1, int var2, Object var3, int var4) {
        if (var4 == 16) {
            blockEncrypt(var0, var1, var2, var3);
        } else {
            Object[] var5 = (Object[])((Object[])var3);
            int[][] var6 = (int[][])((int[][])var5[0]);
            int var7 = var4 / 4;
            int var8 = var6.length - 1;
            int var9 = var7 == 4 ? 0 : (var7 == 6 ? 1 : 2);
            int var10 = shifts[var9][1][0];
            int var11 = shifts[var9][2][0];
            int var12 = shifts[var9][3][0];
            int[] var13 = new int[var7];
            int[] var14 = new int[var7];
            int var16 = 0;

            int var15;
            for(var15 = 0; var15 < var7; ++var15) {
                var14[var15] = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[0][var15];
            }

            for(int var18 = 1; var18 < var8; ++var18) {
                for(var15 = 0; var15 < var7; ++var15) {
                    var13[var15] = T1[var14[var15] >>> 24 & 255] ^ T2[var14[(var15 + var10) % var7] >>> 16 & 255] ^ T3[var14[(var15 + var11) % var7] >>> 8 & 255] ^ T4[var14[(var15 + var12) % var7] & 255] ^ var6[var18][var15];
                }

                System.arraycopy(var13, 0, var14, 0, var7);
            }

            for(var15 = 0; var15 < var7; ++var15) {
                int var17 = var6[var8][var15];
                var1[var16++] = (byte)(S[var14[var15] >>> 24 & 255] ^ var17 >>> 24);
                var1[var16++] = (byte)(S[var14[(var15 + var10) % var7] >>> 16 & 255] ^ var17 >>> 16);
                var1[var16++] = (byte)(S[var14[(var15 + var11) % var7] >>> 8 & 255] ^ var17 >>> 8);
                var1[var16++] = (byte)(S[var14[(var15 + var12) % var7] & 255] ^ var17);
            }

        }
    }

    public static final void blockDecrypt(byte[] var0, byte[] var1, int var2, Object var3, int var4) {
        if (var4 == 16) {
            blockDecrypt(var0, var1, var2, var3);
        } else {
            Object[] var5 = (Object[])((Object[])var3);
            int[][] var6 = (int[][])((int[][])var5[1]);
            int var7 = var4 / 4;
            int var8 = var6.length - 1;
            int var9 = var7 == 4 ? 0 : (var7 == 6 ? 1 : 2);
            int var10 = shifts[var9][1][1];
            int var11 = shifts[var9][2][1];
            int var12 = shifts[var9][3][1];
            int[] var13 = new int[var7];
            int[] var14 = new int[var7];
            int var16 = 0;

            int var15;
            for(var15 = 0; var15 < var7; ++var15) {
                var14[var15] = ((var0[var2++] & 255) << 24 | (var0[var2++] & 255) << 16 | (var0[var2++] & 255) << 8 | var0[var2++] & 255) ^ var6[0][var15];
            }

            for(int var18 = 1; var18 < var8; ++var18) {
                for(var15 = 0; var15 < var7; ++var15) {
                    var13[var15] = T5[var14[var15] >>> 24 & 255] ^ T6[var14[(var15 + var10) % var7] >>> 16 & 255] ^ T7[var14[(var15 + var11) % var7] >>> 8 & 255] ^ T8[var14[(var15 + var12) % var7] & 255] ^ var6[var18][var15];
                }

                System.arraycopy(var13, 0, var14, 0, var7);
            }

            for(var15 = 0; var15 < var7; ++var15) {
                int var17 = var6[var8][var15];
                var1[var16++] = (byte)(Si[var14[var15] >>> 24 & 255] ^ var17 >>> 24);
                var1[var16++] = (byte)(Si[var14[(var15 + var10) % var7] >>> 16 & 255] ^ var17 >>> 16);
                var1[var16++] = (byte)(Si[var14[(var15 + var11) % var7] >>> 8 & 255] ^ var17 >>> 8);
                var1[var16++] = (byte)(Si[var14[(var15 + var12) % var7] & 255] ^ var17);
            }

        }
    }

    private static boolean self_test(int var0) {
        boolean var1 = false;

        try {
            byte[] var2 = new byte[var0];
            byte[] var3 = new byte[16];

            int var4;
            for(var4 = 0; var4 < var0; ++var4) {
                var2[var4] = (byte)var4;
            }

            for(var4 = 0; var4 < 16; ++var4) {
                var3[var4] = (byte)var4;
            }

            Object var5 = makeKey(var2, 16);
            byte[] var6 = new byte[16];
            blockEncrypt(var3, var6, 0, var5, 16);
            byte[] var7 = new byte[16];
            blockDecrypt(var6, var7, 0, var5, 16);
            var1 = areEqual(var3, var7);
            if (!var1) {
                throw new RuntimeException("Symmetric operation failed");
            }
        } catch (Exception var8) {
        }

        return var1;
    }

    public static final int getRounds(int var0, int var1) {
        switch (var0) {
            case 16:
                return var1 == 16 ? 10 : (var1 == 24 ? 12 : 14);
            case 24:
                return var1 != 32 ? 12 : 14;
            default:
                return 14;
        }
    }

    private static final boolean areEqual(byte[] var0, byte[] var1) {
        int var2 = var0.length;
        if (var2 != var1.length) {
            return false;
        } else {
            for(int var3 = 0; var3 < var2; ++var3) {
                if (var0[var3] != var1[var3]) {
                    return false;
                }
            }

            return true;
        }
    }

    private static final String byteToString(int var0) {
        char[] var1 = new char[]{HEX_DIGITS[var0 >>> 4 & 15], HEX_DIGITS[var0 & 15]};
        return new String(var1);
    }

    private static final String intToString(int var0) {
        char[] var1 = new char[8];

        for(int var2 = 7; var2 >= 0; --var2) {
            var1[var2] = HEX_DIGITS[var0 & 15];
            var0 >>>= 4;
        }

        return new String(var1);
    }

    private static final String toString(byte[] var0) {
        int var1 = var0.length;
        char[] var2 = new char[var1 * 2];
        int var3 = 0;

        byte var5;
        for(int var4 = 0; var3 < var1; var2[var4++] = HEX_DIGITS[var5 & 15]) {
            var5 = var0[var3++];
            var2[var4++] = HEX_DIGITS[var5 >>> 4 & 15];
        }

        return new String(var2);
    }

    private static final String toString(int[] var0) {
        int var1 = var0.length;
        char[] var2 = new char[var1 * 8];
        int var3 = 0;

        for(int var4 = 0; var3 < var1; ++var3) {
            int var5 = var0[var3];
            var2[var4++] = HEX_DIGITS[var5 >>> 28 & 15];
            var2[var4++] = HEX_DIGITS[var5 >>> 24 & 15];
            var2[var4++] = HEX_DIGITS[var5 >>> 20 & 15];
            var2[var4++] = HEX_DIGITS[var5 >>> 16 & 15];
            var2[var4++] = HEX_DIGITS[var5 >>> 12 & 15];
            var2[var4++] = HEX_DIGITS[var5 >>> 8 & 15];
            var2[var4++] = HEX_DIGITS[var5 >>> 4 & 15];
            var2[var4++] = HEX_DIGITS[var5 & 15];
        }

        return new String(var2);
    }

    public static void main(String[] var0) {
        self_test(16);
        self_test(24);
        self_test(32);
    }

    static {
        long var0 = System.currentTimeMillis();
        short var2 = 283;
        boolean var4 = false;
        alog[0] = 1;

        int var3;
        int var17;
        for(var3 = 1; var3 < 256; ++var3) {
            var17 = alog[var3 - 1] << 1 ^ alog[var3 - 1];
            if ((var17 & 256) != 0) {
                var17 ^= var2;
            }

            alog[var3] = var17;
        }

        for(var3 = 1; var3 < 255; log[alog[var3]] = var3++) {
        }

        byte[][] var5 = new byte[][]{{1, 1, 1, 1, 1, 0, 0, 0}, {0, 1, 1, 1, 1, 1, 0, 0}, {0, 0, 1, 1, 1, 1, 1, 0}, {0, 0, 0, 1, 1, 1, 1, 1}, {1, 0, 0, 0, 1, 1, 1, 1}, {1, 1, 0, 0, 0, 1, 1, 1}, {1, 1, 1, 0, 0, 0, 1, 1}, {1, 1, 1, 1, 0, 0, 0, 1}};
        byte[] var6 = new byte[]{0, 1, 1, 0, 0, 0, 1, 1};
        byte[][] var8 = new byte[256][8];
        var8[1][7] = 1;

        int var7;
        for(var3 = 2; var3 < 256; ++var3) {
            var17 = alog[255 - log[var3]];

            for(var7 = 0; var7 < 8; ++var7) {
                var8[var3][var7] = (byte)(var17 >>> 7 - var7 & 1);
            }
        }

        byte[][] var9 = new byte[256][8];

        for(var3 = 0; var3 < 256; ++var3) {
            for(var7 = 0; var7 < 8; ++var7) {
                var9[var3][var7] = var6[var7];

                for(var17 = 0; var17 < 8; ++var17) {
                    var9[var3][var7] = (byte)(var9[var3][var7] ^ var5[var7][var17] * var8[var3][var17]);
                }
            }
        }

        for(var3 = 0; var3 < 256; ++var3) {
            S[var3] = (byte)(var9[var3][0] << 7);

            for(var7 = 1; var7 < 8; ++var7) {
                byte[] var10000 = S;
                var10000[var3] = (byte)(var10000[var3] ^ var9[var3][var7] << 7 - var7);
            }

            Si[S[var3] & 255] = (byte)var3;
        }

        byte[][] var10 = new byte[][]{{2, 1, 1, 3}, {3, 2, 1, 1}, {1, 3, 2, 1}, {1, 1, 3, 2}};
        byte[][] var11 = new byte[4][8];

        for(var3 = 0; var3 < 4; ++var3) {
            for(var17 = 0; var17 < 4; ++var17) {
                var11[var3][var17] = var10[var3][var17];
            }

            var11[var3][var3 + 4] = 1;
        }

        byte[][] var14 = new byte[4][4];

        for(var3 = 0; var3 < 4; ++var3) {
            byte var12 = var11[var3][var3];
            if (var12 == 0) {
                for(var7 = var3 + 1; var11[var7][var3] == 0 && var7 < 4; ++var7) {
                }

                if (var7 == 4) {
                    throw new RuntimeException("G matrix is not invertible");
                }

                for(var17 = 0; var17 < 8; ++var17) {
                    byte var13 = var11[var3][var17];
                    var11[var3][var17] = var11[var7][var17];
                    var11[var7][var17] = var13;
                }

                var12 = var11[var3][var3];
            }

            for(var17 = 0; var17 < 8; ++var17) {
                if (var11[var3][var17] != 0) {
                    var11[var3][var17] = (byte)alog[(255 + log[var11[var3][var17] & 255] - log[var12 & 255]) % 255];
                }
            }

            for(var7 = 0; var7 < 4; ++var7) {
                if (var3 != var7) {
                    for(var17 = var3 + 1; var17 < 8; ++var17) {
                        var11[var7][var17] = (byte)(var11[var7][var17] ^ mul(var11[var3][var17], var11[var7][var3]));
                    }

                    var11[var7][var3] = 0;
                }
            }
        }

        for(var3 = 0; var3 < 4; ++var3) {
            for(var17 = 0; var17 < 4; ++var17) {
                var14[var3][var17] = var11[var3][var17 + 4];
            }
        }

        for(var7 = 0; var7 < 256; ++var7) {
            byte var15 = S[var7];
            T1[var7] = mul4(var15, var10[0]);
            T2[var7] = mul4(var15, var10[1]);
            T3[var7] = mul4(var15, var10[2]);
            T4[var7] = mul4(var15, var10[3]);
            var15 = Si[var7];
            T5[var7] = mul4(var15, var14[0]);
            T6[var7] = mul4(var15, var14[1]);
            T7[var7] = mul4(var15, var14[2]);
            T8[var7] = mul4(var15, var14[3]);
            U1[var7] = mul4(var7, var14[0]);
            U2[var7] = mul4(var7, var14[1]);
            U3[var7] = mul4(var7, var14[2]);
            U4[var7] = mul4(var7, var14[3]);
        }

        rcon[0] = 1;
        int var16 = 1;

        for(var7 = 1; var7 < 30; rcon[var7++] = (byte)(var16 = mul(2, var16))) {
        }

        var0 = System.currentTimeMillis() - var0;
    }
}
