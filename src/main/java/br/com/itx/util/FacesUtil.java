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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Classe genérica para acesso ao container do myfaces.
 */
public final class FacesUtil {
    
    private static final LogProvider LOG = Logging.getLogProvider(FacesUtil.class);
	
	private FacesUtil() {}
	
	/**
     * Recupera um ServltContext do builder.
     *
     * @param webapp define o contexto a ser recuperado.
     */    
	public static ServletContext getServletContext(String webapp) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		ServletContext wiSc = (ServletContext) ec.getContext();
		if (webapp == null) {
			return wiSc;
		}
		return wiSc.getContext(webapp);
	}	
	
	/**
     * Recupera uma mensagem.
     *
     * @param bundle define o arquivo de mensagens a ser utilizado.
     * @param key define a chave a ser utilizada.
     */    
	public static String getMessage(String bundle, String key) {	
		FacesContext fc = FacesContext.getCurrentInstance();
		Locale loc = fc.getViewRoot().getLocale();
		ResourceBundle rb = ResourceBundle.getBundle(bundle, loc);
        return rb.getString(key);
	}

	/**
     * Recupera o outputstream já com o mime definido.
     * 
     * @param mime define o mime a ser enviado.
     * @param filename define o nome ao salvar o arquivo.
     */    
	public static OutputStream getOutputStream(boolean nocache,
			String mime, String name) {
    	FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		HttpServletResponse response = (HttpServletResponse)ec.getResponse();
        if (nocache) {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "must-revalidate, no-store");
            response.setDateHeader("Expires", 0);
        } else {
        	response.setHeader("Cache-Control", "max-age=60");
        }
        if (name != null && !name.equals("")) {
        	String disposition = "inline; filename=\"" + name + "\"";
        	response.setHeader("Content-disposition", disposition);
        }	
		response.setContentType(mime);
		OutputStream out = null;
		try {
			out = response.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return out;
	}

	/**
     * Fecha o outputstream.
     */    
	public static void closeOutputStream(OutputStream out) {
    	try {
    		if (out != null) {
    			out.flush();
    			out.close();
    		}
    	} catch (IOException e) { 
    		e.printStackTrace(System.out);
    	}	
	}
	
	/**
     * Armazena uma mensagem de erro.
     *
     * @param message define a mensagem.
     */    
	public static void setErrorMessage(String message) {
	  	try {
	  		String encmsg = URLEncoder.encode(message, "iso-8859-1");
	  		encmsg = encmsg.replace('+', ' ');
			Contexts.getEventContext().set("errorMessage", encmsg);
		} catch (UnsupportedEncodingException e) {
		    LOG.error(".setErrorMessage()", e);
		}		
	}
	/**
     * Clona um objeto.
     */    
	@SuppressWarnings("unchecked")
	public static <T extends Object> T cloneBean(T obj) {
    	Object resp = null;
	    try {
	    	byte[] bytes = null;
	    	// Serialize to a byte array
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutput out = new ObjectOutputStream(baos);
	        out.writeObject(obj);
	        out.close();
	        bytes = baos.toByteArray();
	        // Deserialize from a byte array
	        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	        ObjectInputStream in = new ObjectInputStream(bais);
	        resp = in.readObject();
	        in.close();
	    } catch (Exception e) {
    		e.printStackTrace(System.out);
	    }
	    return (T)resp;
	}
	
}