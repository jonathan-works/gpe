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

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.component.suggest.AbstractSuggestBean;
import br.com.infox.ibpm.entity.GrupoModeloDocumento;


@Name(GrupoModeloDocumentoSuggestBean.NAME)
@BypassInterceptors
@Install(precedence=Install.FRAMEWORK)
public class GrupoModeloDocumentoSuggestBean extends AbstractSuggestBean<GrupoModeloDocumento> {
	public static final String NAME = "grupoModeloDocumentoSuggest"; 

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getEjbql() {
		StringBuilder sb = new StringBuilder();
		sb.append("select o ");
		sb.append("from GrupoModeloDocumento o ");
		sb.append("where lower(grupoModeloDocumento) ");
		sb.append("like lower(concat ('%', :");
		sb.append(INPUT_PARAMETER);
		sb.append(", '%')) ");
		sb.append("and o.ativo = true order by 1");
		return sb.toString();
	}
	
	@Override
	protected String getEventSelected() {
		return "gurpoModeloDocumentoChangedEvent";
	}

}