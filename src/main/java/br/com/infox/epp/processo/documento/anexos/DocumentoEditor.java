package br.com.infox.epp.processo.documento.anexos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Scope(ScopeType.CONVERSATION)
@Name(DocumentoEditor.NAME)
public class DocumentoEditor extends DocumentoCreator implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "documentoEditor";
    private static final LogProvider LOG = Logging.getLogProvider(DocumentoEditor.class);

    @In
    private DocumentoManager documentoManager;
    private List<Integer> idsDocumentosSessao;
    
    private boolean expanded = false;

    @Override
    protected LogProvider getLogger() {
        return LOG;
    }

    @Override
    protected Documento gravarDocumento() throws DAOException {
    	if (getDocumento().getId() == null) {
    		return documentoManager.gravarDocumentoNoProcesso(getProcesso(), getDocumento(), getPasta());
    	} else {
    		return documentoManager.update(getDocumento());
    	}
    }
    
    @Override
    public void persist() {
    	super.persist();
    	Integer id = getDocumentosDaSessao().get(getDocumentosDaSessao().size() - 1).getId();
    	if (!idsDocumentosSessao.contains(id)) {
    		idsDocumentosSessao.add(id);
    	}
    	reloadDocumentos();
    }

    @Override
    public void clear() {
    	super.clear();
    	idsDocumentosSessao = new ArrayList<>();
    	idsDocumentosSessao.add(-1);
    	setPasta(null);
    	reloadDocumentos();
    }
    
    public boolean isExpanded() {
		return expanded;
	}
    
    public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
    
    @Override
    public void setDocumento(Documento documento) {
    	super.setDocumento(documento);
    	if (documento.getId() != null) {
    		setExpanded(true);
    	}
    }
    
    private void reloadDocumentos() {
    	setDocumentosDaSessao(new ArrayList<Documento>());
    	getDocumentosDaSessao().addAll(documentoManager.getDocumentosSessaoAnexar(getProcesso(), idsDocumentosSessao));
    }

    
}
