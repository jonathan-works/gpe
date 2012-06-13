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
package br.com.infox.ibpm.entity.log;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.itx.util.EntityUtil;


@Name("entityLogQuery")
@BypassInterceptors
public class EntityLogQuery {

	@SuppressWarnings("unchecked")
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