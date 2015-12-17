package br.com.infox.epp.processo.comunicacao.manager;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.action.DestinatarioBean;
import br.com.infox.epp.processo.comunicacao.dao.ModeloComunicacaoDAO;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;

@Stateless
public class ModeloComunicacaoManager extends Manager<ModeloComunicacaoDAO, ModeloComunicacao> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "modeloComunicacaoManager";
	
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	
	public boolean isExpedida(ModeloComunicacao modeloComunicacao) {
		return getDao().isExpedida(modeloComunicacao);
	}
	
	public boolean hasComunicacaoExpedida(ModeloComunicacao modeloComunicacao) {
		return getDao().hasComunicacaoExpedida(modeloComunicacao);
	}
	
	public List<ModeloComunicacao> listModelosComunicacaoPorProcessoRoot(String processoRoot) {
		return getDao().listModelosComunicacaoPorProcessoRoot(processoRoot);
	}
	
	public DocumentoModeloComunicacao getDocumentoInclusoPorPapel(Collection<String> identificadoresPapel, ModeloComunicacao modeloComunicacao) {
		return getDao().getDocumentoInclusoPorPapel(identificadoresPapel, modeloComunicacao);
	}
	
	public List<Documento> getDocumentosByModeloComunicacao(ModeloComunicacao modeloComunicacao){
		return getDao().getDocumentosByModeloComunicacao(modeloComunicacao);
	}
	
	public List<DestinatarioBean> listDestinatarios(String numeroProcessoRoot) {
		List<DestinatarioBean> destinatarios = getDao().listDestinatarios(numeroProcessoRoot);
		EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);
		for (DestinatarioBean destinatario : destinatarios) {
			DestinatarioModeloComunicacao destinatarioModeloComunicacao = entityManager.find(DestinatarioModeloComunicacao.class, destinatario.getIdDestinatario());
			entityManager.refresh(destinatarioModeloComunicacao);
			Processo comunicacao = destinatarioModeloComunicacao.getProcesso();
			destinatario.setNome(destinatarioModeloComunicacao.getNome());
			destinatario.setStatusProrrogacao(prazoComunicacaoService.getStatusProrrogacaoFormatado(comunicacao));
			destinatario.setDataConfirmacao(getMetadadoValue(comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA)));
			destinatario.setDataResposta(getMetadadoValue(comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_RESPOSTA)));
			destinatario.setPrazoAtendimento(getMetadadoValue(comunicacao.getMetadado(ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO)));
			destinatario.setPrazoFinal(getMetadadoValue(comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO)));
			destinatario.setPrazoOriginal(getMetadadoValue(comunicacao.getMetadado(ComunicacaoMetadadoProvider.LIMITE_DATA_CUMPRIMENTO_INICIAL)));
			destinatario.setResponsavelConfirmacao(getMetadadoValue(comunicacao.getMetadado(ComunicacaoMetadadoProvider.RESPONSAVEL_CIENCIA)));
			setarInformacoesAdicionais(destinatario);
		}
		return destinatarios;
	}
	
	public List<AssinaturaDocumento> listAssinaturasComunicacao(Processo comunicacao) {
		CriteriaBuilder cb = getDao().getEntityManager().getCriteriaBuilder();
		CriteriaQuery<AssinaturaDocumento> query = cb.createQuery(AssinaturaDocumento.class);
		Root<DestinatarioModeloComunicacao> dmc= query.from(DestinatarioModeloComunicacao.class);
		Join<DestinatarioModeloComunicacao, Documento> doc = dmc.join(DestinatarioModeloComunicacao_.documentoComunicacao);
		Join<Documento, DocumentoBin> bin = doc.join(Documento_.documentoBin);
		Join<DocumentoBin, AssinaturaDocumento> assin = bin.join(DocumentoBin_.assinaturas, JoinType.LEFT);
		query.where(cb.equal(dmc.get(DestinatarioModeloComunicacao_.processo), comunicacao));
		query.orderBy(cb.desc(assin.get(AssinaturaDocumento_.dataAssinatura)));
		query.select(assin);
		return getDao().getEntityManager().createQuery(query).getResultList();
	}
	
	protected void setarInformacoesAdicionais(DestinatarioBean destinatario) {
	}
	
	public void removerDocumentosRelacionados(ModeloComunicacao modeloComunicacao) throws DAOException {
		for (DocumentoModeloComunicacao docComunicacao : modeloComunicacao.getDocumentos()) {
			getDao().getEntityManager().remove(docComunicacao);
		}
	}
	
	public void removerDestinatariosModelo(ModeloComunicacao modeloComunicacao) throws DAOException {
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			getDao().getEntityManager().remove(destinatario);
		}
	}

	protected String getMetadadoValue(MetadadoProcesso metadado) {
		if (metadado == null) {
			return "-";
		}
		Object value = metadado.getValue();
		if (value instanceof Date) {
			return new SimpleDateFormat("dd/MM/yyyy").format(value);
		}
		return value.toString();
	}
	
	public String getNomeVariavelModeloComunicacao(Long idModeloComunicacao) {
		return getDao().getNomeVariavelModeloComunicacao(idModeloComunicacao);
	}
}