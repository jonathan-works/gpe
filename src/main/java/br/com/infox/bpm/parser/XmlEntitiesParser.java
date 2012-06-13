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
package br.com.infox.bpm.parser;

import java.util.HashMap;
import java.util.Map;

public class XmlEntitiesParser {

    private static final Map<String, String> decoder = new HashMap<String, String>(300);

    static {
    	add("&nbsp","&#160");
    	add("&iexcl","&#161");
    	add("&cent","&#162");
    	add("&pound","&#163");
    	add("&curren","&#164");
    	add("&yen","&#165");
    	add("&brvbar","&#166");
    	add("&sect","&#167");
    	add("&uml","&#168");
    	add("&copy","&#16;");
    	add("&ordf","&#170");
    	add("&laquo","&#17");
    	add("&not","&#172");
    	add("&shy","&#173");
    	add("&reg","&#174");
    	add("&macr","&#175");
    	add("&deg","&#176");
    	add("&plusmn","&#177");
    	add("&sup2","&#178");
    	add("&sup3","&#179");
    	add("&acute","&#180");
    	add("&micro","&#181");
    	add("&para","&#182");
    	add("&middot","&#183");
    	add("&cedil","&#184");
    	add("&sup1","&#185");
    	add("&ordm","&#186");
    	add("&raquo","&#187");
    	add("&frac14","&#188");
    	add("&frac12","&#189");
    	add("&frac34","&#190");
    	add("&iquest","&#191");
    	add("&Agrave","&#192");
    	add("&Aacute","&#193");
    	add("&Acirc","&#194");
    	add("&Atilde","&#195");
    	add("&Auml","&#196");
    	add("&Aring","&#197");
    	add("&AElig","&#198");
    	add("&Ccedil","&#199");
    	add("&Egrave","&#200");
    	add("&Eacute","&#201");
    	add("&Ecirc","&#202");
    	add("&Euml","&#203");
    	add("&Igrave","&#204");
    	add("&Iacute","&#205");
    	add("&Icirc","&#206");
    	add("&Iuml","&#207");
    	add("&ETH","&#208");
    	add("&Ntilde","&#209");
    	add("&Ograve","&#210");
    	add("&Oacute","&#211");
    	add("&Ocirc","&#212");
    	add("&Otilde","&#213");
    	add("&Ouml","&#214");
    	add("&times","&#215");
    	add("&Oslash","&#216");
    	add("&Ugrave","&#217");
    	add("&Uacute","&#218");
    	add("&Ucirc","&#219");
    	add("&Uuml","&#220");
    	add("&Yacute","&#221");
    	add("&THORN","&#222");
    	add("&szlig","&#223");
    	add("&agrave","&#224");
    	add("&aacute","&#225");
    	add("&acirc","&#226");
    	add("&atilde","&#227");
    	add("&auml","&#228");
    	add("&aring","&#229");
    	add("&aelig","&#230");
    	add("&ccedil","&#231");
    	add("&egrave","&#232");
    	add("&eacute","&#233");
    	add("&ecirc","&#234");
    	add("&euml","&#235");
    	add("&igrave","&#236");
    	add("&iacute","&#237");
    	add("&icirc","&#238");
    	add("&iuml","&#239");
    	add("&eth","&#240");
    	add("&ntilde","&#241");
    	add("&ograve","&#242");
    	add("&oacute","&#243");
    	add("&ocirc","&#244");
    	add("&otilde","&#245");
    	add("&ouml","&#246");
    	add("&divide","&#247");
    	add("&oslash","&#248");
    	add("&ugrave","&#249");
    	add("&uacute","&#250");
    	add("&ucirc","&#251");
    	add("&uuml","&#252");
    	add("&yacute","&#253");
    	add("&thorn","&#254");
    	add("&yuml","&#255");
    }  
    
    public static String parse(String s) {
    	for(java.util.Map.Entry<String, String> e : decoder.entrySet()) {
    		s = s.replaceAll(e.getKey(), e.getValue());
    	}
    	return s;
    }
    
    private static final void add(String entity, String value) {
        decoder.put(entity, value);
    }

}