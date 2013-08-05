/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da InformaÁ„o Ltda.

 Este programa È software livre; vocÍ pode redistribuÌ-lo e/ou modific·-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers„o 2 da LicenÁa.
 Este programa È distribuÌdo na expectativa de que seja ˙til, porÈm, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implÌcita de COMERCIABILIDADE OU 
 ADEQUA«√O A UMA FINALIDADE ESPECÕFICA.
 
 Consulte a GNU GPL para mais detalhes.
 VocÍ deve ter recebido uma cÛpia da GNU GPL junto com este programa; se n„o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.util;

import java.text.ParseException;
import java.util.List;

import org.jboss.seam.util.Strings;


public final class StringUtil {
	
	private StringUtil() { }
	
    private static final String FOREIGN_CHARS = 
    	"·¡È…ÌÕÛ”˙⁄‡¿Ë»ÏÃÚ“˘Ÿ‚¬Í ÓŒÙ‘˚€‰ƒÎÀÔœˆ÷¸‹„√ı’Á«Ò—";
    
    private static final String US_CHARS =
        "aAeEiIoOuUaAeEiIoOuUaAeEiIoOuUaAeEiIoOuUaAoOcCnN";

	
	public static String changeChar(String text, char c1, String c2) {
		StringBuffer aux = new StringBuffer();
		for (int i=0; i < text.length(); i++) {
			char c = text.charAt(i); 
			if (c == c1) {
				aux.append(c2);
			} else {
				aux.append(c);
			}
		}
		return aux.toString();
	}
	
    public static String changeChars(String text, 
    		String oldChars, String newChars) {
        StringBuffer aux = new StringBuffer();
        char let;
        for (int i = 0; i < text.length(); i++) {
            let = text.charAt(i);
            int pos = oldChars.indexOf(let);
            if (pos == -1) {
                aux.append(let);
            } else if (newChars.length() > pos) {
                aux.append(newChars.charAt(pos));
            }
        }
        return aux.toString();
    }

	public static String piece(String text, String delim, int p) {
		return piece(text, delim, p, p);
	}
	
	public static String piece(String text, String delim, final int p1, final int p2) {
	    int l1 = p1;
	    int l2 = p2;
		if ((text == null) || (text.length() == 0)) { return ""; }
		if ((delim == null) || (delim.length() == 0)) { return text; }
		if (l1 < 1) { l1 = 1; } 
		if (l2 < 0) { l2 = 0; }
		if ((l2 != 0) && (l2 < l1)) { return ""; }
		int piece = 1;
		int ini = 0;
		int pos = 0;
		int fim = text.length();
		pos = text.indexOf(delim);
		while (((piece <= l2) || (l2 == 0)) && (pos > -1)) {
			if ((l2 > 0) && (piece == l2)) {
				fim = pos;
			}
			piece++;
			if (piece == l1) {
				ini = pos + delim.length();
			}
			pos = text.indexOf(delim, pos + delim.length());
		}
		if (piece < l1) { return ""; }
		return text.substring(ini, fim);
	}
	
    public static String replace(String subject, String find, String replace) {
        StringBuffer buf = new StringBuffer();
        int lengthSubject = find.length();
        int posAux = 0;
        int posFind = subject.indexOf(find);

        while (posFind != -1) {
            buf.append(subject.substring(posAux, posFind));
            buf.append(replace);
            posAux = posFind + lengthSubject;
            posFind = subject.indexOf(find, posAux);
        }

        buf.append(subject.substring(posAux));
        return buf.toString();
    }	
	
	
    /**
     * Elimina acentuaÁ„o do texto
     * @param text
     * @return o texto sem os caracteres acentuados
     */
    public static String getUsAscii(String text) {
        return changeChars(text, FOREIGN_CHARS, US_CHARS);
    }
    
    public static String retiraZerosEsquerda(String numero) {
    	if (Strings.isEmpty(numero) || !numero.startsWith("0")) {
    		return numero;
    	}
    	char[] charArray = numero.toCharArray();
    	int posUltimoZero = 0;
    	for (char c : charArray) {
			if (c != '0') {
				break;
			}
			posUltimoZero++;
		}
    	return numero.substring(posUltimoZero);
    }
    
    public static String limparCharsNaoNumericos(String s) {
    	//TODO rever esse nome
    	return s.replaceAll("[^0-9]", "");
    }
    
    public static String replaceQuebraLinha(String texto) {
    	if (Strings.isEmpty(texto)) {
    		return texto;
    	} else {
    		String saida = texto.replace("\\015","");
    		saida = saida.replace("\\012","");
    		saida = saida.replace("\n","");
    		saida = saida.replace("\r","");
    		return saida;
    	}
    }    
	
    public static String removeNaoNumericos(String source) {
    	return source.replaceAll("\\D", "");
    }
    
    public static String removeNaoAlphaNumericos(String source) {
    	return source.replaceAll("\\d", "");
    }    
    
    public static String capitalizeAllWords(String words){
    	StringBuilder out = new StringBuilder();
    	if(!Strings.isEmpty(words)){
    		String[] wordList = words.trim().split(" ");
    		for (int i = 0; i < wordList.length; i++) {
    			if(wordList[i].length() > 1){
				  wordList[i] = wordList[i].substring(0,1).toUpperCase() + wordList[i].substring(1).toLowerCase();
    			} else {
    			  wordList[i] = wordList[i].toUpperCase();
    			}
    			out.append(wordList[i]);
    			out.append(" ");
			}
    	}
    	return out.toString().trim();
    }
    
    public static String completaZeros(String numero, int tamanho) {
    	StringBuilder sb = new StringBuilder();
    	int zerosAdicionar = tamanho - numero.length();
    	while (sb.length() < zerosAdicionar) {
    		sb.append('0');
    	}
    	sb.append(numero);
    	return sb.toString();
    }

    public static String formatNumericString(String string, String mask)
    		throws java.text.ParseException {
    	javax.swing.text.MaskFormatter mf = new javax.swing.text.MaskFormatter(mask);
    	mf.setValueContainsLiteralCharacters(false);
    	return mf.valueToString(string);
    }	
    
    public static String formartCpf(String cpf) {
    	try {
			return formatNumericString(cpf, "###.###.###-##");
		} catch (ParseException e) {
			return null;
		}
    }
    
    public static String formatCnpj(String cnpj) {
    	try {
			return formatNumericString(cnpj, "##.###.###/####-##");
		} catch (ParseException e) {
			return null;
		}
    }
    
    public static String concatList(List<Object> list, String delimitador) {
    	StringBuilder sb = new StringBuilder();
    	for (Object object : list) {
			if (sb.length() > 0) {
				sb.append(delimitador);
			}
			sb.append(object);
		}
    	return sb.toString();
    }
    
    public static String toLowerCaseFirstChar(String string) {
    	if (string == null || string.length() == 0) {
    		return string;
    	} else {
    		char[] charArray = string.toCharArray();
    		charArray[0] = Character.toLowerCase(charArray[0]);
    		return String.valueOf(charArray);
    	}
	}
    
    public static String padRight(String s, int n) {
    	return String.format("%1$-" + n + "s", s);  
    }

    public static String padLeft(String s, int n) {
    	return String.format("%1$#" + n + "s", s);  
    }    

}