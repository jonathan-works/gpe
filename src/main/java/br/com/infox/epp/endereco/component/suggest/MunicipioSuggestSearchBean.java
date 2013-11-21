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

import br.com.infox.epp.endereco.entity.Estado;
import br.com.infox.epp.endereco.list.CepList;


@Name(MunicipioSuggestSearchBean.NAME)
@Scope(ScopeType.EVENT)
public class MunicipioSuggestSearchBean extends MunicipioSuggestBean {
 
	public static final String NAME = "municipioSearchSuggest";
	private static final long serialVersionUID = 1L;

	@Override
	public Estado getEstado() {
        return CepList.instance().getEstado();
	}
}