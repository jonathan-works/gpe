package br.com.infox.epp.access.util.quartzo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.BitSet;

public class HexUtil {
    private HexUtil() {
    }

    public static final String bytesToHex(byte[] var0, int var1, int var2) {
        StringBuffer var3 = new StringBuffer(var2 * 2);
        bytesToHexAppend(var0, var1, var2, var3);
        return var3.toString();
    }

    public static final void bytesToHexAppend(byte[] var0, int var1, int var2, StringBuffer var3) {
        var3.ensureCapacity(var3.length() + var2 * 2);

        for(int var4 = var1; var4 < var1 + var2 && var4 < var0.length; ++var4) {
            var3.append(Character.forDigit(var0[var4] >>> 4 & 15, 16));
            var3.append(Character.forDigit(var0[var4] & 15, 16));
        }

    }

    public static final String bytesToHex(byte[] var0) {
        return bytesToHex(var0, 0, var0.length);
    }

    public static final byte[] hexToBytes(String var0) {
        return hexToBytes(var0, 0);
    }

    public static final byte[] hexToBytes(String var0, int var1) {
        byte[] var2 = new byte[var1 + (1 + var0.length()) / 2];
        hexToBytes(var0, var2, var1);
        return var2;
    }

    public static final void hexToBytes(String var0, byte[] var1, int var2) throws NumberFormatException, IndexOutOfBoundsException {
        int var3 = var0.length();
        if (var3 % 2 != 0) {
            var0 = '0' + var0;
        }

        if (var1.length < var2 + var3 / 2) {
            throw new IndexOutOfBoundsException("Output buffer too small for input (" + var1.length + "<" + var2 + var3 / 2 + ")");
        } else {
            for(int var6 = 0; var6 < var3; var6 += 2) {
                byte var4 = (byte)Character.digit(var0.charAt(var6), 16);
                byte var5 = (byte)Character.digit(var0.charAt(var6 + 1), 16);
                if (var4 < 0 || var5 < 0) {
                    throw new NumberFormatException();
                }

                var1[var2 + var6 / 2] = (byte)(var4 << 4 | var5);
            }

        }
    }

    public static final byte[] bitsToBytes(BitSet var0, int var1) {
        int var2 = countBytesForBits(var1);
        byte[] var3 = new byte[var2];
        Object var4 = null;

        for(int var5 = 0; var5 < var3.length; ++var5) {
            short var6 = 0;

            for(int var7 = 0; var7 < 8; ++var7) {
                int var8 = var5 * 8 + var7;
                boolean var9 = var8 > var1 ? false : var0.get(var8);
                var6 = (short)(var6 | (var9 ? 1 << var7 : 0));
                if (var4 != null) {
                    ((StringBuffer)var4).append((char)(var9 ? '1' : '0'));
                }
            }

            if (var6 > 255) {
                throw new IllegalStateException("WTF? s = " + var6);
            }

            var3[var5] = (byte)var6;
        }

        return var3;
    }

    public static final String bitsToHexString(BitSet var0, int var1) {
        return bytesToHex(bitsToBytes(var0, var1));
    }

    public static int countBytesForBits(int var0) {
        return var0 / 8 + (var0 % 8 == 0 ? 0 : 1);
    }

    public static void writeBigInteger(BigInteger var0, DataOutputStream var1) throws IOException {
        if (var0.signum() == -1) {
            throw new IllegalStateException("Negative BigInteger!");
        } else {
            byte[] var2 = var0.toByteArray();
            if (var2.length > 32767) {
                throw new IllegalStateException("Too long: " + var2.length);
            } else {
                var1.writeShort((short)var2.length);
                var1.write(var2);
            }
        }
    }

    public static BigInteger readBigInteger(DataInputStream var0) throws IOException {
        short var1 = var0.readShort();
        if (var1 < 0) {
            throw new IOException("Invalid BigInteger length: " + var1);
        } else {
            byte[] var2 = new byte[var1];
            var0.readFully(var2);
            return new BigInteger(1, var2);
        }
    }

    public static String biToHex(BigInteger var0) {
        return bytesToHex(var0.toByteArray());
    }

    public static boolean InHexFormat(String var0) {
        for(int var1 = 0; var1 < var0.length(); ++var1) {
            if (!IsHexDigit(var0.charAt(var1))) {
                return false;
            }
        }

        return true;
    }

    public static boolean IsHexDigit(char var0) {
        int var2 = Character.digit('A', 32);
        int var3 = Character.digit('0', 32);
        var0 = (new String(new char[]{var0})).toUpperCase().charAt(0);
        int var1 = Character.digit(var0, 32);
        if (var1 >= var2 && var1 < var2 + 6) {
            return true;
        } else {
            return var1 >= var3 && var1 < var3 + 10;
        }
    }
}
