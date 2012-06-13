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
package br.com.infox.ibpm.component.suggest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.component.suggest.AbstractSuggestBean;
import br.com.infox.ibpm.entity.Estado;
import br.com.infox.ibpm.entity.Municipio;
import br.com.infox.ibpm.home.CepHome;


@Name(MunicipioSuggestBean.NAME)
@Scope(ScopeType.PAGE)
@BypassInterceptors
@Install(precedence=Install.FRAMEWORK)
public class MunicipioSuggestBean extends AbstractSuggestBean<Municipio> {
 
	public static final String NAME = "municipioSuggest";
	private static final long serialVersionUID = 1L;

	public String getEjbql() {
		Estado estado = getEstado();
		if(estado == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select o from Municipio o ");
		sb.append("where o.estado.idEstado = ");
		sb.append(estado.getIdEstado());
		sb.append(" and ");
		sb.append("lower(o.municipio) like lower(concat(:");
		sb.append(INPUT_PARAMETER);
		sb.append(", '%'))) ");
		sb.append("order by 1");
		return  sb.toString();
	}

	public Estado getEstado() {
		return CepHome.instance().getEstado();
	}
	
}