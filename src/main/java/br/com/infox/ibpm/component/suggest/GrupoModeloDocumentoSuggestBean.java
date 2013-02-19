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