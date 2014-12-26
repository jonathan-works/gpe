package br.com.infox.core.file.encode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class MD5Encoder {

    private static final LogProvider LOG = Logging.getLogProvider(MD5Encoder.class);

    private MD5Encoder() {

    }

    /**
     * Codifica um texto para o formato MD5.
     * 
     * @param text o texto a ser codificado.
     * @return o texto no formato MD5.
     */
    public static String encode(String text) {
        return encode(text.getBytes());
    }

    /**
     * Codifica um texto para o formato MD5.
     * 
     * @param array a ser codificado.
     * @return o texto no formato MD5.
     */
    public static String encode(byte[] bytes) {
        StringBuilder resp = new StringBuilder();
        if (bytes != null) {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] hash = digest.digest(bytes);
                for (int i = 0; i < hash.length; i++) {
                    if (((int) hash[i] & 0xff) < 0x10) {
                        resp.append("0");
                    }
                    resp.append(Long.toString((int) hash[i] & 0xff, 16));
                }
            } catch (NoSuchAlgorithmException err) {
                LOG.error(".encodeMD5()", err);
                // Nunca deve ocorrer.
            }
        }
        return resp.toString();
    }
}
