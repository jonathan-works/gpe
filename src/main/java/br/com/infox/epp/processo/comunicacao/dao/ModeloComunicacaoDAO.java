package br.com.infox.epp.processo.comunicacao.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.action.DestinatarioBean;
import br.com.infox.epp.processo.comunicacao.query.ModeloComunicacaoQuery;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.system.Parametros;

@Name(ModeloComunicacaoDAO.NAME)
@AutoCreate
public class ModeloComunicacaoDAO extends DAO<ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoDAO";
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		return getNamedSingleResult(ModeloComunicacaoQuery.IS_EXPEDIDA, params) == null;
	}

	public boolean hasComunicacaoExpedida(ModeloComunicacao modeloComunicacao) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		return getNamedSingleResult(ModeloComunicacaoQuery.HAS_COMUNICACAO_EXPEDIDA, params) != null;
	}
	
	public List<ModeloComunicacao> listModelosComunicacaoPorProcessoRoot(String processoRoot) {
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_NUMERO_PROCESSO_ROOT, processoRoot);
		return getNamedResultList(ModeloComunicacaoQuery.LIST_BY_PROCESSO_ROOT, params);
	}
	
	public List<DestinatarioBean> listDestinatarios(String numeroProcessoRoot) {
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DestinatarioBean> query = cb.createQuery(DestinatarioBean.class);
		Root<DestinatarioModeloComunicacao> destinatario = query.from(DestinatarioModeloComunicacao.class);
		Join<DestinatarioModeloComunicacao, Processo> comunicacao = destinatario.join(DestinatarioModeloComunicacao_.processo);
		Join<DestinatarioModeloComunicacao, ModeloComunicacao> modelo = destinatario.join(DestinatarioModeloComunicacao_.modeloComunicacao);
		Join<ModeloComunicacao, TipoComunicacao> tipoComunicacao = modelo.join(ModeloComunicacao_.tipoComunicacao);

		query.select(cb.construct(getClassDestinatarioBean(), tipoComunicacao.get(TipoComunicacao_.descricao), 
			comunicacao.get(Processo_.dataInicio), destinatario.get(DestinatarioModeloComunicacao_.id), 
			comunicacao.get(Processo_.idProcesso).as(String.class), modelo.get(ModeloComunicacao_.id).as(String.class), 
			destinatario.get(DestinatarioModeloComunicacao_.meioExpedicao)));
		query.where(cb.equal(cb.function("NumeroProcessoRoot", String.class, comunicacao.get(Processo_.idProcesso)), numeroProcessoRoot));
		boolean usuarioInterno = Identity.instance().hasRole(Parametros.PAPEL_USUARIO_INTERNO.getValue());
		if (!usuarioInterno) {
			Subquery<Integer> subquery = query.subquery(Integer.class);
			Root<MetadadoProcesso> metadado = subquery.from(MetadadoProcesso.class);
			subquery.where(cb.equal(metadado.get(MetadadoProcesso_.processo), comunicacao),
				cb.equal(metadado.get(MetadadoProcesso_.metadadoType), ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType()));
			subquery.select(cb.literal(1));
			
			query.where(query.getRestriction(), cb.exists(subquery));
		}
		return entityManager.createQuery(query).getResultList();
	}
	
	protected Class<? extends DestinatarioBean> getClassDestinatarioBean() {
		return DestinatarioBean.class;
	}

	public DocumentoModeloComunicacao getDocumentoInclusoPorPapel(Collection<String> identificadoresPapel, ModeloComunicacao modeloComunicacao) {
		TypedQuery<DocumentoModeloComunicacao> q = getEntityManager().createNamedQuery(ModeloComunicacaoQuery.GET_DOCUMENTO_INCLUSO_POR_PAPEL, DocumentoModeloComunicacao.class);
		q.setParameter(ModeloComunicacaoQuery.PARAM_IDENTIFICADORES_PAPEL, identificadoresPapel);
		q.setParameter(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		q.setMaxResults(1);
		List<DocumentoModeloComunicacao> result = q.getResultList();
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}
	
	public List<Documento> getDocumentosByModeloComunicacao(ModeloComunicacao modeloComunicacao){
		Map<String, Object> params = new HashMap<>();
		params.put(ModeloComunicacaoQuery.PARAM_MODELO_COMUNICACAO, modeloComunicacao);
		return getNamedResultList(ModeloComunicacaoQuery.GET_DOCUMENTOS_MODELO_COMUNICACAO, params);
	}
	

	@SuppressWarnings("unchecked")
	public String getNomeVariavelModeloComunicacao(Long idModeloComunicacao) {
		Query query = getEntityManager().createNamedQuery(ModeloComunicacaoQuery.GET_NOME_VARIAVEL_MODELO_COMUNICACAO);
		query.setParameter(1, idModeloComunicacao);
		query.setMaxResults(1);
		List<String> variavel = query.getResultList(); 
		return !variavel.isEmpty() ? variavel.get(0) : null;
	}

}
