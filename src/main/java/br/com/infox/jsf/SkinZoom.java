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
package br.com.infox.jsf;

import java.util.Arrays;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.util.Strings;

import br.com.itx.component.Util;

@Name("skinZoom")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class SkinZoom extends Selector 
{	
	private static final long serialVersionUID = 1L;
	private String skinZoom = "";	
	private static final String TAM_NORMAL = "";
	private static final String TAM_MEDIO = "18px";
	private static final String TAM_GRANDE = "22px";
	private static final String[] TAMANHOS = {"","18px", "22px"}; 
	
	public SkinZoom() 
	{
		Util util = (Util) Component.getInstance("util");
		String cookiePath = util.getContextPath();
		setCookiePath(cookiePath);
		setCookieEnabled(true);
		String tamanhoCookie = getCookieValueIfEnabled();
		if (!Strings.isEmpty(tamanhoCookie)) {
			int i = Arrays.binarySearch(TAMANHOS, tamanhoCookie);
			if (i > -1) {
				skinZoom = tamanhoCookie;
			}
		}
	}

	public String getSkinZoom() {
		return skinZoom;
	}
	
	public void setTmNormal()
	{
		setCookieValueIfEnabled(TAM_NORMAL);
		skinZoom = TAM_NORMAL;	
	}
	
	public void setTmMedio()
	{
		setCookieValueIfEnabled(TAM_MEDIO);
	    skinZoom = TAM_MEDIO;	
	}
	
	public void setTmGrande()
	{
		setCookieValueIfEnabled(TAM_GRANDE);
	    skinZoom = TAM_GRANDE;	
	}

	@Override
	protected String getCookieName() {
		return "br.com.infox.jsf";
	}
}



