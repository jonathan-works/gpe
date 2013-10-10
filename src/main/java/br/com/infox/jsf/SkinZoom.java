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
	private String skinZoom = TAM_NORMAL;	
	private static final String TAM_NORMAL = "12px";
	private static final String TAM_MEDIO = "18px";
	private static final String TAM_GRANDE = "22px";
	private static final String[] TAMANHOS = {TAM_NORMAL,TAM_MEDIO, TAM_GRANDE}; 
	
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



