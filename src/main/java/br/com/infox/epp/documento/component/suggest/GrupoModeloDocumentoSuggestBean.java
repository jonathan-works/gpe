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
package br.com.infox.epp.documento.component.suggest;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.documento.entity.GrupoModeloDocumento;


@Name(GrupoModeloDocumentoSuggestBean.NAME)
@Install(precedence=Install.FRAMEWORK)
public class GrupoModeloDocumentoSuggestBean extends AbstractSuggestBean<GrupoModeloDocumento> {
	public static final String NAME = "grupoModeloDocumentoSuggest"; 

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getEjbql() {
		StringBuilder sb = new StringBuilder();
		sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idGrupoModeloDocumento, o.grupoModeloDocumento) ");
		sb.append("from GrupoModeloDocumento o ");
		sb.append("where lower(grupoModeloDocumento) ");
		sb.append("like lower(concat ('%', :");
		sb.append(INPUT_PARAMETER);
		sb.append(", '%')) ");
		sb.append("and o.ativo = true order by 1");
		return sb.toString();
	}

    @Override
    public GrupoModeloDocumento load(Object id) {
        return entityManager.find(GrupoModeloDocumento.class, id);
    }
	
}