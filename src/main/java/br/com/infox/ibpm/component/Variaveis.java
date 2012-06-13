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
package br.com.infox.ibpm.component;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


@Name("variaveis")
@Scope(ScopeType.APPLICATION)
@Install(precedence=Install.FRAMEWORK)
public class Variaveis {
	
	private static final String FACELETS_PARAM_DEVELOPMENT = "facelets.DEVELOPMENT";

	@Factory(scope =  ScopeType.STATELESS)
	public String getDataAtual() {
		Locale ptBR = new Locale("pt", "BR");
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, ptBR);
		return dateFormat.format(new Date());
	}

	@Factory(scope =  ScopeType.APPLICATION, value = "desenvolvimento")
	public boolean isDesenvolvimento() {
		String initParameter = FacesContext.getCurrentInstance()
			.getExternalContext().getInitParameter(FACELETS_PARAM_DEVELOPMENT);
		return "true".equalsIgnoreCase(initParameter);
	}
}