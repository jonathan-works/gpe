package br.com.infox.epp.processo.documento.anexos;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.log.LogProvider;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

public abstract class DocumentoCreator {
    
	private Processo processo;
    private Documento documento;
    private List<Documento> documentosDaSessao;
    private Pasta pasta;
    
    private Processo processoReal;
    
    private MetadadoProcessoManager metadadoProcessoManager = ComponentUtil.getComponent(MetadadoProcessoManager.NAME, ScopeType.EVENT);

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
    	if (processo == null) {
    		this.processo = null;
    		setProcessoReal(null);
    	} else {
    		this.processo = processo.getProcessoRoot();
    		this.setProcessoReal(processo);
    	}
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public List<Documento> getDocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setDocumentosDaSessao(List<Documento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }

    public void newInstance() {
        setDocumento(new Documento());
        getDocumento().setAnexo(true);
        getDocumento().setDocumentoBin(new DocumentoBin());
        List<MetadadoProcesso> metaPastas = metadadoProcessoManager.getMetadadoProcessoByType(getProcessoReal(), EppMetadadoProvider.PASTA_DEFAULT.getMetadadoType());
        if (!metaPastas.isEmpty()) {
        	setPasta((Pasta)metaPastas.get(0).getValue());
        }
    }

    public void clear() {
        setDocumentosDaSessao(new ArrayList<Documento>());
        newInstance();
    }

    public void persist() {
        try {
        	Documento documento = gravarDocumento();
            getDocumentosDaSessao().add(documento);
        } catch (DAOException | BusinessException e) {
            getLogger().error("Não foi possível gravar o documento "
                    + getDocumento() + " no processo " + getProcesso(), e);
        }
        newInstance();
    }

    protected abstract LogProvider getLogger();

    protected abstract Documento gravarDocumento() throws DAOException;
    
    public Pasta getPasta() {
        return pasta;
    }

    public void setPasta(Pasta pasta) {
    	if (pasta == null) {
    		return;
    	}
        this.pasta = pasta;
    }

	public Processo getProcessoReal() {
		return processoReal;
	}

	public void setProcessoReal(Processo processoReal) {
		this.processoReal = processoReal;
	}

}
