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
package br.com.itx.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.util.Strings;

import br.com.itx.component.Util;

@Name("wiSkin")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class Skin extends Selector {

	private static final long serialVersionUID = 1L;

	private String skin = "skin/azul";
	
	private List<SelectItem> skins;
	
	private static final String[] SKINS = {"verde", "azul", "cinza",
		"altoContraste"}; 
	
	public Skin() {
		Util util = (Util) Component.getInstance("util");
		String cookiePath = util.getContextPath();
		setCookiePath(cookiePath);
		setCookieEnabled(true);
		String skinCookie = getCookieValueIfEnabled();
		if (!Strings.isEmpty(skinCookie)) {
			if (skinCookie.startsWith("skin/")) {
				for(String arg : SKINS) {
					if (arg.equalsIgnoreCase(skinCookie.substring(5))) {
						skin = skinCookie;
						break;
					}
				}
			}
		}
	}
	
	public String getSkin() {
		return skin;
	}
	
	public void setSkin(String skin) {
		setCookieValueIfEnabled(skin);		
		this.skin = skin;
	}
	
	public List<SelectItem> getSkinList() {
		if (skins != null) {
			return skins;
		}
		skins = new ArrayList<SelectItem>();
		for (String s : SKINS) {
			skins.add(new SelectItem("skin/" + s, s));
		}
		return skins;
	}

	@Override
	protected String getCookieName() {
		return "br.com.infox.ibpm.skin";
	}
	
}