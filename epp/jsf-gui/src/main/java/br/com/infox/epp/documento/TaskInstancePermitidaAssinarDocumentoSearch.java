package br.com.infox.epp.documento;

import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.TaskInstancePermitidaAssinarDocumento;
import br.com.infox.epp.documento.entity.TaskInstancePermitidaAssinarDocumento_;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoBin_;
import br.com.infox.epp.processo.documento.entity.Documento_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskInstancePermitidaAssinarDocumentoSearch extends PersistenceController {
	
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	
	public TaskInstanceListagemDocumentoDTO getListaDocumentoDTOParaSeremAssinados(List<String> listaIdTaskInstance) {
		TaskInstanceListagemDocumentoDTO listagemDocumentoDTO = new TaskInstanceListagemDocumentoDTO();
		for(String idTaskInstance : listaIdTaskInstance) {
			for(Documento documento : getListaDocumentosParaSeremAssinados(idTaskInstance)) {
				if (documento.getDocumentoBin().getMinuta()) {
					listagemDocumentoDTO.getListaDocumentoMinuta().add(documento);
				} else if (assinaturaDocumentoService.podeRenderizarApplet(Authenticator.getPapelAtual(),
						documento.getClassificacaoDocumento(),
						documento.getDocumentoBin(), Authenticator.getUsuarioLogado())) {
					listagemDocumentoDTO.getListaDocumentoAssinavel().add(documento);
				} else {
					listagemDocumentoDTO.getListaDocumentoNaoAssinavel().add(documento);
				}
			}
		}
		return listagemDocumentoDTO;
	}
	
	public List<Documento> getListaDocumentosParaSeremAssinados(String idTaskInstance) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Documento> query = cb.createQuery(Documento.class);
        Root<TaskInstancePermitidaAssinarDocumento> taskPermitida = query.from(TaskInstancePermitidaAssinarDocumento.class);
        Join<TaskInstancePermitidaAssinarDocumento, Documento> documento = taskPermitida.join(TaskInstancePermitidaAssinarDocumento_.documento);
        Join<Documento, DocumentoBin> documentoBin = documento.join(Documento_.documentoBin);
        query.where(cb.equal(taskPermitida.get(TaskInstancePermitidaAssinarDocumento_.idTaskInstance), Long.valueOf(idTaskInstance)), cb.isFalse(documentoBin.get(DocumentoBin_.suficientementeAssinado)));
        query.select(documento);
        
        List<Documento> listaDocumento = getEntityManager().createQuery(query).getResultList();
        
        for(Iterator<Documento> itDocumento = listaDocumento.iterator(); itDocumento.hasNext();) {
        	Documento doc = itDocumento.next();
        	boolean usuarioPodeAssinar = assinaturaDocumentoService.isAssinavelPorUsuarioAtual(Authenticator.getPapelAtual(), doc.getClassificacaoDocumento(), doc.getDocumentoBin(), Authenticator.getUsuarioLogado());
        	if(!usuarioPodeAssinar) {
        		itDocumento.remove();
        	}
        }
        
        return listaDocumento;
	}
	
	public boolean taskPossuiDocumentoParaSerAssinado(String idTaskInstance) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<TaskInstancePermitidaAssinarDocumento> taskPermitida = query.from(TaskInstancePermitidaAssinarDocumento.class);
        Join<TaskInstancePermitidaAssinarDocumento, Documento> documento = taskPermitida.join(TaskInstancePermitidaAssinarDocumento_.documento);
        Join<Documento, DocumentoBin> documentoBin = documento.join(Documento_.documentoBin);
        query.where(cb.equal(taskPermitida.get(TaskInstancePermitidaAssinarDocumento_.idTaskInstance), Long.valueOf(idTaskInstance)), cb.isFalse(documentoBin.get(DocumentoBin_.suficientementeAssinado)));
        query.select(cb.count(taskPermitida));
        return getEntityManager().createQuery(query).getSingleResult() > 0;
	}

}
