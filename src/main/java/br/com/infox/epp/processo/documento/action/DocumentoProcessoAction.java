package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.HistoricoStatusDocumentoManager;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.Parametros;

@AutoCreate
@Name(DocumentoProcessoAction.NAME)
@Scope(ScopeType.PAGE)
public class DocumentoProcessoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoProcessoAction";
	
	private String motivoExclusaoRestauracao;
	private Documento processoDocumentoSelected;
	private Integer idDocumentoAlter;
	private Map<String, Boolean> cache = new HashMap<String, Boolean>();
	private List<ClassificacaoDocumento> listClassificacaoDocumento;
	private ClassificacaoDocumento classificacaoDocumentoItem;
	private Processo processo;
	
	@In
	private DocumentoManager documentoManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private HistoricoStatusDocumentoManager historicoStatusDocumentoManager;
	@In
	private ClassificacaoDocumentoManager classificacaoDocumentoManager;
	@In
	private DocumentoList documentoList;
	
	public void exclusaoRestauracaoDocumento(){
		if (idDocumentoAlter == null){
			FacesMessages.instance().add("Não existe documento para alterar");
		} else {
			Documento documento = documentoManager.find(idDocumentoAlter);
			TipoAlteracaoDocumento tipoAlteracaoDocumento = documento.getExcluido() ? TipoAlteracaoDocumento.R : TipoAlteracaoDocumento.E;
			try {
				documentoManager.exclusaoRestauracaoLogicaDocumento(documento, getMotivoExclusaoRestauracao(), tipoAlteracaoDocumento);
				FacesMessages.instance().add("#{infoxMessages['ProcessoDocumento_updated']}");
				if (cache.containsKey(idDocumentoAlter.toString())){
					cache.remove(idDocumentoAlter.toString());
				}
			} catch (DAOException e) {
				actionMessagesService.handleDAOException(e);
			}
		}
	}
	
	public void onClickDocumentosTab(){
		cache.clear();
	}
	
	public String getMotivoExclusaoRestauracao() {
		return motivoExclusaoRestauracao;
	}
	
	public void setMotivoExclusaoRestauracao(String motivoExclusaoRestauracao) {
		this.motivoExclusaoRestauracao = motivoExclusaoRestauracao;
	}
	
	public Documento getProcessoDocumentoSelected() {
		return processoDocumentoSelected;
	}
	
	public void setProcessoDocumentoSelected(Documento processoDocumentoSelected) {
		this.processoDocumentoSelected = processoDocumentoSelected;
	}
	
	public void setIdDocumento(Integer idDocumento) {
		if ( idDocumento <= 0 || 
				(processoDocumentoSelected != null && idDocumento.equals(processoDocumentoSelected.getId()))) {
			processoDocumentoSelected = null;
		} else {
			processoDocumentoSelected = documentoManager.find(idDocumento);
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
		return Identity.instance().hasPermission("/pages/Processo/excluirDocumentoProcesso", "access");
	}
	
	public boolean podeUsuarioVerHistorico(){
		return !("true".equals(Parametros.SOMENTE_USUARIO_INTERNO_PODE_VER_HISTORICO.getValue()) && !Identity.instance().hasRole("usuarioInterno"));
	}

	public List<ClassificacaoDocumento> getListClassificacaoDocumento() {
		if (listClassificacaoDocumento == null) {
			listClassificacaoDocumento = classificacaoDocumentoManager.getClassificacaoDocumentoListByProcesso(processo);
		}
			
		return listClassificacaoDocumento;
	}
	
	public void setListClassificacaoDocumento(List<ClassificacaoDocumento> listClassificacaoDocumento) {
		this.listClassificacaoDocumento = listClassificacaoDocumento;
	}

	public Integer getClassificacaoDocumentoItem() {
		return classificacaoDocumentoItem != null ? classificacaoDocumentoItem.getId() : null;
	}

	public void setClassificacaoDocumentoItem(Integer classificacaoDocumentoItem) {
		if (classificacaoDocumentoItem != null) {
			for (ClassificacaoDocumento classificacaoDocumento : listClassificacaoDocumento) {
				if (classificacaoDocumento.getId().equals(classificacaoDocumentoItem)) {
					this.classificacaoDocumentoItem = classificacaoDocumento;
					break;
				}
			}
		}
		else {
			this.classificacaoDocumentoItem = null;
		}
		documentoList.getEntity().setClassificacaoDocumento(this.classificacaoDocumentoItem);		
	}
	
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
}

