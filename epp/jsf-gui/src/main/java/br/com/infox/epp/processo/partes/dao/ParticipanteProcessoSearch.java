package br.com.infox.epp.processo.partes.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo_;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;
import br.com.infox.epp.pessoa.entity.Pessoa_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ParticipanteProcessoSearch extends PersistenceController {

	public List<Processo> getListaProcessoSemSigiloPor(String cpfParticipante, Fluxo fluxo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Processo> query = cb.createQuery(Processo.class);
        Root<Processo> processo = query.from(Processo.class);
        Root<PessoaFisica> pessoaFisica = query.from(PessoaFisica.class);
        Join<?, ParticipanteProcesso> participanteProcesso = processo.join(Processo_.participantes, JoinType.INNER);
        Join<?, Pessoa> pessoa = participanteProcesso.join(ParticipanteProcesso_.pessoa, JoinType.INNER);
        Join<?, NaturezaCategoriaFluxo> ncf = processo.join(Processo_.naturezaCategoriaFluxo, JoinType.INNER);
        
        query.select(processo);
        query.where(
        		cb.equal(ncf.get(NaturezaCategoriaFluxo_.fluxo), fluxo),
                cb.equal(pessoaFisica.get(PessoaFisica_.cpf), cpfParticipante),
                cb.equal(pessoa.get(Pessoa_.idPessoa), pessoaFisica.get(PessoaFisica_.idPessoa))
                );
        return getEntityManager().createQuery(query).getResultList();
	}
}
