/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.util;

import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Classe utilizada para criptogratia de textos.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.3 $
 *
 * @since 3.0
 */
public class Crypto {
    private SecretKey desKey;
    
    private static final LogProvider LOG = Logging.getLogProvider(Crypto.class);

    /**
     * Cria um novo Crypto.
     *
     * @param key a chave que será usada no DES.
     */
    public Crypto(String key) {
        String strKey = (key == null ? "" : key);
        try {
            KeyGenerator kg = KeyGenerator.getInstance("DES", "SunJCE");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(strKey.getBytes());            
            kg.init(sr);
            desKey = kg.generateKey();
        } catch (Exception err) {
            LOG.error(" new Crypto()", err);
            // Nunca deve ocorrer.
        }
    }

    /**
     * Codifica um texto para o formato DES.
     *
     * @param text o texto a ser codificado.
     *
     * @return o texto no formato DES.
     */
    public String encodeDES(String text) {
        StringBuffer resp = new StringBuffer();
        if (text != null) {
            try {
                Cipher cipher = Cipher.getInstance("DES", "SunJCE");
                cipher.init(Cipher.ENCRYPT_MODE, desKey);
                byte[] enc = cipher.doFinal(text.getBytes());
                for (int i = 0; i < enc.length; i++) {
                    if (((int) enc[i] & 0xff) < 0x10) {
                        resp.append("0");
                    }
                    resp.append(Long.toString((int) enc[i] & 0xff, 16));
                }
            } catch (Exception err) {
                LOG.error(".encodeDES()", err);
                // Nunca deve ocorrer.
            }
        }
        return resp.toString();
    }

    /**
     * Decodifica um texto no formato DES.
     *
     * @param text o texto no formato DES.
     *
     * @return o texto decodificado.
     */
    public String decodeDES(String text) {
        if (text == null) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance("DES", "SunJCE");
            AlgorithmParameters algParams = cipher.getParameters();
            cipher.init(Cipher.DECRYPT_MODE, desKey, algParams);
            int size = text.length() / 2;
            byte[] msg = new byte[size];
            for (int i = 0; i < (size * 2); i = i + 2) {
                String hex = text.substring(i, i + 2);
                msg[i / 2] = (byte) (Integer.parseInt(hex, 16));
            }
            byte[] dec = cipher.doFinal(msg);
            return new String(dec).trim();
        } catch (Exception err) {
        	String message = err.getMessage() + " in password \"" + text + "\"";
        	LOG.error(message);
        	LOG.error(".descodeDES()", err);
        }
        return "";
    }

    /**
     * Codifica um texto para o formato MD5.
     *
     * @param text o texto a ser codificado.
     *
     * @return o texto no formato MD5.
     */
    public static String encodeMD5(String text) {
        return encodeMD5(text.getBytes());
    }

    /**
     * Codifica um texto para o formato MD5.
     *
     * @param array a ser codificado.
     *
     * @return o texto no formato MD5.
     */
    public static String encodeMD5(byte[] bytes) {
        StringBuffer resp = new StringBuffer();
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