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
package br.com.infox.ibpm.entity.log;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.EntityUtil;


@Name("entityLogQuery")
@BypassInterceptors
public class EntityLogQuery {

	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<EntityLog> getResultList(Class<?> classEntidade, Object id) {
		EntityManager manager = EntityUtil.getEntityManager();
		List<EntityLog> list = null;
		StringBuilder sb = new StringBuilder();
		sb.append("select o from EntityLog o ");
		sb.append("where o.nomeEntidade = :nomeClasse ");
		sb.append("o.nomePackage = :nomePackage ");
		sb.append((id == null ? "" : "and o.idEntidade = :id "));
		Query q = manager.createQuery(sb.toString());
		q.setParameter("nomeClasse", classEntidade.getName());
		q.setParameter("nomePackage", classEntidade.getPackage().getName());
		if (id != null) {
			q.setParameter("id", id.toString());
		}
		list = q.getResultList();
		return list;
	}	
	
}