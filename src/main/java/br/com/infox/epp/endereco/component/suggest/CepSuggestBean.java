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
package br.com.infox.epp.endereco.component.suggest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.epp.endereco.entity.Cep;


@Name(CepSuggestBean.NAME)
@Scope(ScopeType.CONVERSATION)
public class CepSuggestBean extends AbstractSuggestBean<Cep> {
 
	public static final String NAME = "cepSuggest";
    private static final long serialVersionUID = 1L;

	@Override
	public String getEjbql() {
		StringBuilder sb = new StringBuilder();
		sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idCep, o.cep) from Cep o ");
		sb.append("where lower(numeroCep) like lower(concat (:");
		sb.append(INPUT_PARAMETER);
		sb.append(", '%')) ");
		sb.append("order by 1");
		return sb.toString();
	}
	
	@Override
	public Cep load(Object id) {
	    return entityManager.find(Cep.class, id);
	}
}