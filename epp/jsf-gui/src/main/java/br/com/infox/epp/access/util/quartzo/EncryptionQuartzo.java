package br.com.infox.epp.access.util.quartzo;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Random;

public class EncryptionQuartzo {

    public static String AJAX_ENCRYPTION_KEY = "GX_AJAX_KEY";
    public static String AJAX_SECURITY_TOKEN = "AJAX_SECURITY_TOKEN";
    public static String GX_AJAX_PRIVATE_KEY = "595D54FF4A612E69FF4F3FFFFF0B01FF";
    private static final int CHECKSUM_LENGTH = 6;
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static Random random = new Random();

    public EncryptionQuartzo() {
    }

    public static String encrypt64(String var0, String var1) {
        int var2 = var1.lastIndexOf(46);
        if (var2 > 0) {
            var1 = var1.substring(0, var2);
        }

        if (var1.length() != 32) {
            throw new RuntimeException("Invalid key");
        } else {
            try {
                return new String(Codecs.base64Encode(encrypt(var0.getBytes("UTF8"), Twofish_Algorithm.makeKey(convertKey(var1)))));
            } catch (UnsupportedEncodingException var4) {
                System.err.println(var4);
                throw new RuntimeException(var4.getMessage());
            } catch (InvalidKeyException var5) {
                System.err.println(var5);
                throw new RuntimeException(var5.getMessage());
            }
        }
    }

    private static byte[] convertKey(String var0) {
        byte[] var1 = new byte[var0.length() / 2];
        int var2 = 0;

        for(int var3 = 0; var2 < var0.length(); ++var3) {
            var1[var3] = (byte)(toHexa(var0.charAt(var2)) * 16 + toHexa(var0.charAt(var2 + 1)));
            var2 += 2;
        }

        return var1;
    }

    private static byte toHexa(char var0) {
        byte var1;
        if (var0 >= '0' && var0 <= '9') {
            var1 = (byte)(var0 - 48);
        } else if (var0 >= 'a' && var0 <= 'f') {
            var1 = (byte)(var0 - 97 + 10);
        } else {
            if (var0 < 'A' || var0 > 'F') {
                throw new InternalError("invalid key " + var0);
            }

            var1 = (byte)(var0 - 65 + 10);
        }

        return var1;
    }

    public static String encrypt16(String var0, String var1) {
        return "";
    }

    public static String decrypt16(String var0, String var1) {
        return "";
    }

    public static String decrypt64(String var0, String var1) {
        int var2 = var1.lastIndexOf(46);
        if (var2 > 0) {
            var1 = var1.substring(0, var2);
        }

        if (var1.length() != 32) {
            throw new RuntimeException("Invalid key");
        } else {
            var0 = GXutil.rtrim(var0);

            try {
                return GXutil.rtrim(new String(decrypt(Codecs.base64Decode(var0.getBytes()), Twofish_Algorithm.makeKey(convertKey(var1))), "UTF8"));
            } catch (InvalidKeyException var4) {
                System.err.println(var4);
                throw new RuntimeException(var4.getMessage());
            } catch (UnsupportedEncodingException var5) {
                System.err.println(var5);
                throw new RuntimeException(var5.getMessage());
            } catch (ArrayIndexOutOfBoundsException var6) {
                return "";
            }
        }
    }

    public static int getCheckSumLength() {
        return 6;
    }

    public static String calcChecksum(String var0, int var1, int var2, int var3) {
        int var4 = 0;

        for(int var5 = var1; var5 < var2; ++var5) {
            var4 += var0.charAt(var5);
        }

        return GXutil.padl(GXutil.upper(Integer.toHexString(var4)), var3, "0");
    }

    public static String checksum(String var0, int var1) {
        return calcChecksum(var0, 0, var0.length(), var1);
    }

    public static String addchecksum(String var0, int var1) {
        return var0 + calcChecksum(var0, 0, var0.length(), var1);
    }

    public static String getNewKey() {
        char[] var0 = new char[32];
        byte[] var1 = new byte[16];
        random.nextBytes(var1);
        int var2 = 0;

        byte var4;
        for(int var3 = 0; var2 < 16; var0[var3++] = HEX_DIGITS[var4 & 15]) {
            var4 = var1[var2++];
            var0[var3++] = HEX_DIGITS[var4 >>> 4 & 15];
        }

        return new String(var0);
    }

    public static byte[] encrypt(byte[] var0, Object var1) {
        int var2 = 0;
        if (var0.length % 16 != 0) {
            var2 = 16 - var0.length % 16;
        }

        byte[] var3 = new byte[var0.length + var2];
        byte[] var4 = new byte[var3.length];
        System.arraycopy(var0, 0, var3, 0, var0.length);

        int var5;
        for(var5 = 0; var5 < var2; ++var5) {
            var3[var0.length + var5] = 32;
        }

        var5 = var3.length / 16;

        for(int var6 = 0; var6 < var5; ++var6) {
            System.arraycopy(Twofish_Algorithm.blockEncrypt(var3, var6 * 16, var1), 0, var4, var6 * 16, 16);
        }

        return var4;
    }

    private static String toString(byte[] var0) {
        return toString(var0, 0, var0.length);
    }

    private static String toString(byte[] var0, int var1, int var2) {
        char[] var3 = new char[var2 * 2];
        int var4 = var1;

        byte var6;
        for(int var5 = 0; var4 < var1 + var2; var3[var5++] = HEX_DIGITS[var6 & 15]) {
            var6 = var0[var4++];
            var3[var5++] = HEX_DIGITS[var6 >>> 4 & 15];
        }

        return new String(var3);
    }

    private static void printBytes(byte[] var0, int var1, int var2) {
        for(int var3 = var1; var3 < var2; ++var3) {
            System.out.print(var0[var3] + " ");
        }

        System.out.println("");
    }

    public static byte[] decrypt(byte[] var0, Object var1) {
        byte[] var2 = new byte[var0.length];
        int var3 = var0.length / 16;

        for(int var4 = 0; var4 < var3; ++var4) {
            System.arraycopy(Twofish_Algorithm.blockDecrypt(var0, var4 * 16, var1), 0, var2, var4 * 16, 16);
        }

        return var2;
    }

    public static String getRijndaelKey() {
        Random var0 = new Random();
        byte[] var1 = new byte[16];
        var0.nextBytes(var1);
        StringBuffer var2 = new StringBuffer(32);

        for(int var3 = 0; var3 < 16; ++var3) {
            var2.append(GXutil.padl(Integer.toHexString(var1[var3]), 2, "0"));
        }

        return var2.toString().toUpperCase();
    }

    public static String decryptRijndael(String var0, String var1, boolean[] var2) {
        try {
            var2[0] = false;
            byte[] var3 = HexUtil.hexToBytes(var0);
            String var4 = "";
            if (var3.length > 0) {
                byte[] var5 = HexUtil.hexToBytes(var1);
                Object var6 = Rijndael_Algorithm.makeKey(var5);
                int var7 = var3.length / 16;
                if (var3.length % 16 > 0) {
                    ++var7;
                }

                int var8;
                for(var8 = 0; var8 < var7; ++var8) {
                    int var9 = 16 * var8;
                    byte[] var10 = new byte[16];
                    Rijndael_Algorithm.blockDecrypt(var3, var10, var9, var6);
                    var4 = var4 + new String(var10);
                }

                var8 = var4.indexOf(0);
                if (var8 != -1) {
                    var4 = var4.substring(0, var8);
                }

                var2[0] = true;
            }

            return var4;
        } catch (Exception var11) {
            return var0;
        }
    }

    public static String encryptRijndael(String var0, String var1) throws Exception {
        byte[] var2 = var0.getBytes();
        byte[] var3 = HexUtil.hexToBytes(var1);
        Object var4 = Rijndael_Algorithm.makeKey(var3);
        String var5 = "";
        int var6 = var2.length / 16;
        if (var2.length % 16 > 0) {
            ++var6;
        }

        byte[] var7 = new byte[var6 * 16];

        int var8;
        for(var8 = 0; var8 < var6 * 16; ++var8) {
            if (var8 < var2.length) {
                var7[var8] = var2[var8];
            } else {
                var7[var8] = 0;
            }
        }

        for(var8 = 0; var8 < var6; ++var8) {
            int var9 = 16 * var8;
            byte[] var10 = new byte[16];
            Rijndael_Algorithm.blockEncrypt(var7, var10, var9, var4);
            var5 = var5 + HexUtil.bytesToHex(var10);
        }

        return var5.trim();
    }

    static class RandomGenerator extends SecureRandom {
        RandomGenerator() {
        }

        public void nextBytes2(byte[] var1) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
                var1[var2] = (byte)this.next(4);
            }

        }
    }
}
