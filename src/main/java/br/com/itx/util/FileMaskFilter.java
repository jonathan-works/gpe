/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.util;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;

public class FileMaskFilter implements FileFilter {
	
	private String mask;
	private boolean sensitive;

	public FileMaskFilter(String mask, boolean sensitive) {
		if (mask != null && !sensitive) {
			mask = mask.toUpperCase();
		}
		this.mask = mask;
		this.sensitive = sensitive;
	}

	public boolean accept(File file) {
        if (mask == null) {
        	return false;
        }
        String filename = file.getName();
        if (!sensitive) {
            filename = filename.toUpperCase();
        }
        StringTokenizer st = new StringTokenizer(mask, ",");
        while(st.hasMoreTokens()) {
        	String mask = st.nextToken().trim();
        	if (!mask.equals("")) {
        		if (check(filename, mask)) {
        			return true; 
        		}
        	}
        }
        return false;
	}

	private boolean check(String filename, String mask) {
        mask = mask.replace("*.*", "*");
        if (mask.equals("") || mask.equals("*.*")) {
            mask = "*";
        }
        if (mask.equals(filename) || mask.equals("*")) {
            return true;
        }
        boolean resp = false;
        if (mask.indexOf('*') != -1) {
            if (mask.startsWith(filename) && !mask.startsWith(filename + "/")) {
                resp = true;
            } else {
                int pos = mask.indexOf('*');
                String miaux = mask.substring(0, pos);
                String mfaux = mask.substring(pos + 1, mask.length());
                String fiaux = filename.substring(0, miaux.length());
                int len = filename.length() - mfaux.length();
                String ffaux = filename.substring(len, filename.length());
                if ((miaux + mfaux).equals(fiaux + ffaux)) {
                    resp = true;
                }
            }
        }
        return resp;
	}
	
}