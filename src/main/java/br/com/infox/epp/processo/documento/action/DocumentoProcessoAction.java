package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.HistoricoStatusDocumentoManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;
import br.com.infox.epp.system.Parametros;

@Name(DocumentoProcessoAction.NAME)
@Scope(ScopeType.PAGE)
public class DocumentoProcessoAction implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoProcessoAction";
	
	private String motivoExclusaoRestauracao;
	private ProcessoDocumento processoDocumentoSelected;
	private Integer idDocumentoAlter;
	private Map<String, Boolean> cache;
	
	@In
	private ProcessoDocumentoManager processoDocumentoManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private HistoricoStatusDocumentoManager historicoStatusDocumentoManager;
	
	public void exclusaoRestauracaoDocumento(){
		if (idDocumentoAlter == null){
			FacesMessages.instance().add("Não existe documento para alterar");
		} else {
			ProcessoDocumento documento = processoDocumentoManager.find(idDocumentoAlter);
			TipoAlteracaoDocumento tipoAlteracaoDocumento = documento.getExcluido() ? TipoAlteracaoDocumento.R : TipoAlteracaoDocumento.E;
			try {
				processoDocumentoManager.exclusaoRestauracaoLogicaDocumento(documento, getMotivoExclusaoRestauracao(), tipoAlteracaoDocumento);
				FacesMessages.instance().add("#{eppmessages['ProcessoDocumento_updated']}");
				if (cache.containsKey(idDocumentoAlter.toString())){
					cache.remove(idDocumentoAlter.toString());
				}
			} catch (DAOException e) {
				actionMessagesService.handleDAOException(e);
			}
		}
	}
	
	public void onClickDocumentosTab(){
		cache = new HashMap<String, Boolean>();
	}
	
	public String getMotivoExclusaoRestauracao() {
		return motivoExclusaoRestauracao;
	}
	
	public void setMotivoExclusaoRestauracao(String motivoExclusaoRestauracao) {
		this.motivoExclusaoRestauracao = motivoExclusaoRestauracao;
	}
	
	public ProcessoDocumento getProcessoDocumentoSelected() {
		return processoDocumentoSelected;
	}
	
	public void setProcessoDocumentoSelected(ProcessoDocumento processoDocumentoSelected) {
		this.processoDocumentoSelected = processoDocumentoSelected;
	}
	
	public void setIdDocumento(Integer idDocumento) {
		if ( idDocumento <= 0 || 
				(processoDocumentoSelected != null && idDocumento.equals(processoDocumentoSelected.getIdProcessoDocumento()))) {
			processoDocumentoSelected = null;
		} else {
			processoDocumentoSelected = processoDocumentoManager.find(idDocumento);
		}
	}
	
	public Integer getIdDocumentoAlter() {
		return idDocumentoAlter;
	}
	
	public void setIdDocumentoAlter(Integer idDocumentoAlter) {
		this.idDocumentoAlter = idDocumentoAlter;
	}
	
	@SuppressWarnings("unchecked")
	public List<HistoricoStatusDocumento> getListHistoricoStatusDocumento(){
		if (processoDocumentoSelected == null) {
			return Collections.EMPTY_LIST;
		} else {
			return historicoStatusDocumentoManager.getListHistoricoByDocumento(getProcessoDocumentoSelected());
		}
	}
	
	public boolean hasHistoricoDocumento(Integer idDocumento){
		if (!cache.containsKey(idDocumento.toString())){
			boolean value = historicoStatusDocumentoManager.existeAlgumHistoricoDoDocumento(idDocumento);
			cache.put(idDocumento.toString(), value);
		}
		return cache.get(idDocumento.toString());
	}
	
	public boolean podeUsuarioExcluirRestaurar(){
		return Identity.instance().hasPermission("/pages/Processo/excluirDocumento", "access");
	}
	
	public boolean podeUsuarioVerHistorico(){
		return !("true".equals(Parametros.SOMENTE_USUARIO_INTERNO_PODE_VER_HISTORICO.getValue()) && !Identity.instance().hasRole("usuarioInterno"));
	}
	
}
