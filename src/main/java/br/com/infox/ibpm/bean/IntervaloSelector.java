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
package br.com.infox.ibpm.bean;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;

import br.com.itx.component.Util;


@Name("intervaloPainel")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class IntervaloSelector extends Selector {

	private static final long serialVersionUID = 1L;

	private int intervalo = 10;
	
	public IntervaloSelector() {
		Util util = (Util) Component.getInstance("util");
		String cookiePath = util.getContextPath();
		setCookiePath(cookiePath);
		setCookieEnabled(true);
		String intevalo = getCookieValueIfEnabled();
		if (intevalo != null && !intevalo.equals("")) {
			try {
				this.intervalo = Integer.parseInt(intevalo);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getIntervalo() {
		return intervalo;
	}
	
	public void setIntervalo(int intervalo) {
		setCookieValueIfEnabled(intervalo + "");		
		this.intervalo = intervalo;
	}
	
	@Override
	protected String getCookieName() {
		return "br.com.infox.ibpm.intervalo";
	}
	
}