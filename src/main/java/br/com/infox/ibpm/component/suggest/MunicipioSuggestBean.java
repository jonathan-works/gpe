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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.component.suggest.AbstractSuggestBean;
import br.com.infox.ibpm.entity.Estado;
import br.com.infox.ibpm.entity.Municipio;
import br.com.infox.ibpm.home.CepHome;


@Name(MunicipioSuggestBean.NAME)
@Scope(ScopeType.EVENT)
@Install(precedence=Install.FRAMEWORK)
public class MunicipioSuggestBean extends AbstractSuggestBean<Municipio> {
 
	public static final String NAME = "municipioSuggest";
	private static final long serialVersionUID = 1L;

	@Override
	public String getEjbql() {
		Estado estado = getEstado();
		if(estado == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idMunicipio, o.municipio)");
		sb.append(" from Municipio o ");
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
	
	@Override
	public Municipio load(Object id) {
	    return entityManager.find(Municipio.class, id);
	}
}