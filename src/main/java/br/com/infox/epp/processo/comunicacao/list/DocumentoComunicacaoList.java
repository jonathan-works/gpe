package br.com.infox.epp.processo.comunicacao.list;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.processo.documento.entity.Documento;

@Name(DocumentoComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class DocumentoComunicacaoList extends EntityList<Documento> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoComunicacaoList";

	private static final String DEFAULT_EJBQL = "select o from Documento o "
			+ "inner join o.documentoBin bin "
			+ "left join bin.assinaturas a "
			+ "where o.processo = #{documentoComunicacaoList.entity.processo} and o.excluido = false and "
			+ "not exists (select 1 from SigiloDocumento s where s.ativo = true and s.documento = o)";
	
	private static final String DEFAULT_ORDER = "a.dataAssinatura, o.dataInclusao desc";
	
	private Set<Integer> idsDocumentosBin = new HashSet<>();
	
	@Override
	protected void addSearchFields() {
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}
	
	public void adicionarIdDocumentoBin(Integer id) {
		idsDocumentosBin.add(id);
		refreshQuery();
	}
	
	public void removerIdDocumentoBin(Integer id) {
		idsDocumentosBin.remove(id);
		refreshQuery();
	}
	
	private void refreshQuery() {
		StringBuilder hql = new StringBuilder(DEFAULT_EJBQL);
		if (!idsDocumentosBin.isEmpty()) {
			hql.append(" and bin.id not in (");
			Iterator<Integer> it = idsDocumentosBin.iterator();
			while (it.hasNext()) {
				hql.append(it.next());
				if (it.hasNext()) {
					hql.append(",");
				}
			}
			hql.append(")");
		}
		setEjbql(hql.toString());
	}
}
