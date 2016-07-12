package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.HistoricoStatusDocumento;
import br.com.infox.epp.processo.documento.filter.DocumentoFilter;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.HistoricoStatusDocumentoManager;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.marcador.MarcadorSearch;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.security.SecurityUtil;
import br.com.infox.seam.util.ComponentUtil;

@AutoCreate
@Name(DocumentoProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
@ContextDependency
public class DocumentoProcessoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoProcessoAction";
	
	private String motivoExclusaoRestauracao;
	private Documento processoDocumentoSelected;
	private Integer idDocumentoAlter;
	private Map<String, Boolean> cache = new HashMap<String, Boolean>();
	private List<ClassificacaoDocumento> listClassificacaoDocumento;
	private Processo processo;
	private DocumentoFilter documentoFilter = new DocumentoFilter();
	private List<String> identificadoresPapeisHerdeirosUsuarioExterno;
	
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private SecurityUtil securityUtil;
	@Inject
	private HistoricoStatusDocumentoManager historicoStatusDocumentoManager;
	@Inject
	private ClassificacaoDocumentoManager classificacaoDocumentoManager;
	@Inject
	protected MarcadorSearch marcadorSearch; 
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
	
	public List<String> autoCompleteMarcadores(String query) {
	    return marcadorSearch.listByProcessoAndCodigo(getProcesso().getIdProcesso(), query);
	}
	
	public void onClickDocumentosTab(){
		cache.clear();
		documentoList.setProcesso(getProcesso());
		documentoList.refresh();
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
	
	public List<HistoricoStatusDocumento> getListHistoricoStatusDocumento(){
		if (processoDocumentoSelected == null) {
			return Collections.emptyList();
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
	    return securityUtil.checkPage("/pages/Processo/excluirDocumentoProcesso");
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
	
	public void filtrarDocumentos() {
		if (documentoFilter.getIdClassificacaoDocumento() != null) {
			documentoList.setClassificacaoDocumento(classificacaoDocumentoManager.find(documentoFilter.getIdClassificacaoDocumento()));
		} else {
			documentoList.setClassificacaoDocumento(null);
		}
		
		if (documentoFilter.getNumeroDocumento() != null) {
			documentoList.setNumeroDocumento(documentoFilter.getNumeroDocumento());
		} else {
			documentoList.setNumeroDocumento(null);
		}
		
		if (documentoFilter.getMarcadores() != null) {
		    documentoList.setCodigoMarcadores(documentoFilter.getMarcadores());
		} else {
		    documentoList.setCodigoMarcadores(null);
		}
		documentoList.refresh();
	}
	
	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
    public DocumentoFilter getDocumentoFilter() {
		return documentoFilter;
	}

	public boolean isDocumentoInclusoPorUsuarioExterno(Documento documento) {
        if (identificadoresPapeisHerdeirosUsuarioExterno == null) {
            identificadoresPapeisHerdeirosUsuarioExterno = ComponentUtil.<PapelManager>getComponent(PapelManager.NAME).getIdentificadoresPapeisHerdeiros(Parametros.PAPEL_USUARIO_EXTERNO.getValue());
        }
        return ComponentUtil.<DocumentoManager>getComponent(DocumentoManager.NAME).isDocumentoInclusoPorPapeis(documento, identificadoresPapeisHerdeirosUsuarioExterno);
    }
	
	public boolean deveMostrarCadeado(Documento documento) {
		return documento.hasAssinatura() || documento.isDocumentoAssinavel();		
	}
	
	protected Map<String, Boolean> getCache() {
		return cache;
	}
}

