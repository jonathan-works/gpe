package br.com.infox.epp.processo.comunicacao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.system.Parametros;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ProcessoComunicacaoSearch extends PersistenceController {

	@Inject
	private FluxoDAO fluxoDAO;

	public List<Processo> listComunicacoesAguardandoCienciaQualquerMeioExpedicao() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Processo> query = cb.createQuery(Processo.class);
		Root<Processo> processo = query.from(Processo.class);

		Subquery<Integer> subDataCiencia = createSubqueryMetadado(cb, query, processo,
				ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType());

		query.where(cb.isNotNull(processo.get(Processo_.idJbpm)), 
				cb.isNull(processo.get(Processo_.dataFim)),
				cb.exists(subDataCiencia).not(), cb.exists(createSubqueryTipoProcessoComunicacao(cb, query, processo)),
				cb.exists(createSubqueryFluxosComunicacao(cb, query, processo)));

		return getEntityManager().createQuery(query).getResultList();
	}

	public List<Processo> listComunicacoesAguardandoCumprimentoQualquerMeioExpedicao() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Processo> query = cb.createQuery(Processo.class);
		Root<Processo> processo = query.from(Processo.class);

		Subquery<Integer> subComunicacao = createSubqueryTipoProcessoComunicacao(cb, query, processo);
		Subquery<Integer> subDataCiencia = createSubqueryMetadado(cb, query, processo,
				ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType());
		Subquery<Integer> subLimiteCumprimento = createSubqueryMetadado(cb, query, processo,
				ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO.getMetadadoType());
		Subquery<Integer> subDataCumprimento = createSubqueryMetadado(cb, query, processo,
				ComunicacaoMetadadoProvider.DATA_CUMPRIMENTO.getMetadadoType());

		query.where(cb.isNotNull(processo.get(Processo_.idJbpm)), 
				cb.isNull(processo.get(Processo_.dataFim)),
				cb.exists(subDataCiencia),
				cb.exists(subLimiteCumprimento),
				cb.exists(subDataCumprimento).not(),
				cb.exists(subComunicacao));

		return getEntityManager().createQuery(query).getResultList();
	}

	private Subquery<Integer> createSubqueryFluxosComunicacao(CriteriaBuilder cb, CriteriaQuery<Processo> query, Root<Processo> processo) {
		Subquery<Integer> subFluxoAtual = query.subquery(Integer.class);
		Root<ProcessInstance> pi = subFluxoAtual.from(ProcessInstance.class);
		Join<ProcessInstance, ProcessDefinition> pd = pi.join("processDefinition", JoinType.INNER);
		subFluxoAtual.where(cb.equal(pi.get("id"), processo.get(Processo_.idJbpm)),
				pd.get("name").in(getFluxosComunicacaoAtuais()));
		subFluxoAtual.select(cb.literal(1));
		return subFluxoAtual;
	}

	private Subquery<Integer> createSubqueryTipoProcessoComunicacao(CriteriaBuilder cb, CriteriaQuery<Processo> query, Root<Processo> processo) {
		Subquery<Integer> subComunicacao = query.subquery(Integer.class);
		Root<MetadadoProcesso> metadadoTipoProcesso = subComunicacao.from(MetadadoProcesso.class);
		subComunicacao.where(
				cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.processo), processo),
				cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType()),
				cb.or(
						cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.valor), TipoProcesso.COMUNICACAO.toString()),
						cb.equal(metadadoTipoProcesso.get(MetadadoProcesso_.valor), TipoProcesso.COMUNICACAO_NAO_ELETRONICA.toString())
					)
				);
		subComunicacao.select(cb.literal(1));
		return subComunicacao;
	}

	private Subquery<Integer> createSubqueryMetadado(CriteriaBuilder cb, CriteriaQuery<Processo> query,
			Root<Processo> processo, String metadadoType) {
		Subquery<Integer> subMetadado = query.subquery(Integer.class);
		Root<MetadadoProcesso> metadado = subMetadado.from(MetadadoProcesso.class);
		subMetadado.where(cb.equal(metadado.get(MetadadoProcesso_.processo), processo),
				cb.equal(metadado.get(MetadadoProcesso_.metadadoType), metadadoType));
		subMetadado.select(cb.literal(1));
		return subMetadado;
	}

	private List<String> getFluxosComunicacaoAtuais() {
		List<String> descricaoFluxo = new ArrayList<>();
		Fluxo fluxoComunicacao = fluxoDAO.getFluxoByCodigo(Parametros.CODIGO_FLUXO_COMUNICACAO_ELETRONICA.getValue());
		if (fluxoComunicacao != null) {
			descricaoFluxo.add(fluxoComunicacao.getFluxo());
		}
		Fluxo fluxoComunicacaoNaoEletronica = fluxoDAO
				.getFluxoByCodigo(Parametros.CODIGO_FLUXO_COMUNICACAO_NAO_ELETRONICA.getValue());
		if (fluxoComunicacaoNaoEletronica != null) {
			descricaoFluxo.add(fluxoComunicacaoNaoEletronica.getFluxo());
		}
		return descricaoFluxo;
	}
}
