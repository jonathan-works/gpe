package br.com.infox.epp.processo.documento.anexos;

import static br.com.infox.seam.util.ComponentUtil.getComponent;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.controller.AbstractController;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Name(AnexoController.NAME)
@Scope(ScopeType.CONVERSATION)
public class AnexoController extends AbstractController {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "anexoController";

    private Processo processo;
    private List<DocumentoCreator> creators;
    private List<ProcessoDocumento> documentosDaSessao;
    private ProcessoDocumento documentoToExcludeRestore;
    private String motivoExclusaoRestauracao;
    
    @In
    private ProcessoDocumentoManager processoDocumentoManager;
    @In
    private ActionMessagesService actionMessagesService;

    @Create
    public void init() {
        creators = new ArrayList<>();
        creators.add((DocumentoCreator) getComponent(DocumentoUploader.NAME));
        creators.add((DocumentoCreator) getComponent(DocumentoEditor.NAME));
        documentosDaSessao = new ArrayList<>();
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public List<ProcessoDocumento> getdocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setdocumentosDaSessao(List<ProcessoDocumento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }

    public void onClickTabAnexar(Processo processo) {
        for (DocumentoCreator creator : creators) {
            creator.setProcesso(processo);
            creator.clear();
        }
    }
    
    public void exclusaoRestauracaoDocumento(){
    	TipoAlteracaoDocumento tipoAlteracaoDocumento;
    	if (documentoToExcludeRestore.getExcluido() == true){
    		tipoAlteracaoDocumento = TipoAlteracaoDocumento.R;
    	} else {
    		tipoAlteracaoDocumento = TipoAlteracaoDocumento.E;
    	}
    	try {
			processoDocumentoManager.exclusaoRestauracaoLogicaDocumento(getDocumentoToExcludeRestore(), getMotivoExclusaoRestauracao(), tipoAlteracaoDocumento);
			documentoToExcludeRestore = null;
			motivoExclusaoRestauracao = null;
		} catch (DAOException e) {
			actionMessagesService.handleDAOException(e);
		}
    }
    
	public ProcessoDocumento getDocumentoToExcludeRestore() {
		return documentoToExcludeRestore;
	}
	
	public String getMotivoExclusaoRestauracao() {
		return motivoExclusaoRestauracao;
	}

	public void setMotivoExclusaoRestauracao(String motivoExclusaoRestauracao) {
		this.motivoExclusaoRestauracao = motivoExclusaoRestauracao;
	}

	public void setIdDocumentoToExcludeRestore(String idProcessoDocumento){
		if ("null".equals(idProcessoDocumento)){
			documentoToExcludeRestore = null;
			motivoExclusaoRestauracao = null;
		} else {
			documentoToExcludeRestore = processoDocumentoManager.find(Integer.valueOf(idProcessoDocumento));
		}
    }

}
