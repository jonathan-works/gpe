package br.com.infox.epp.processo.comunicacao.list;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;

@Named
@ViewScoped
public class ParticipanteProcessoComunicacaoList extends EntityList<ParticipanteProcesso> {
    
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_EJBQL = "select o from ParticipanteProcesso o "
			+ "where exists (select 1 from PessoaFisica pf where pf = o.pessoa) and "
			+ "o.ativo = true and o.processo = #{participanteProcessoComunicacaoList.entity.processo}";
	
	private static final String DEFAULT_ORDER = "o.caminhoAbsoluto";
	
	private Set<Integer> idsPessoas = new HashSet<>();
	
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
	
	public void adicionarIdPessoa(Integer id) {
		idsPessoas.add(id);
		refreshQuery();
	}
	
	public void removerIdPessoa(Integer id) {
		idsPessoas.remove(id);
		refreshQuery();
	}
	
	public void clearIdPessoa() {
		idsPessoas = new HashSet<>();
		refreshQuery();
	}
	
	private void refreshQuery() {
		StringBuilder hql = new StringBuilder(DEFAULT_EJBQL);
		if (!idsPessoas.isEmpty()) {
			hql.append(" and o.pessoa.idPessoa not in (");
			Iterator<Integer> it = idsPessoas.iterator();
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
