package br.com.infox.epp.processo.documento.manager;

import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoPapelManager;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.dao.DocumentoDAO;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.numeration.NumeracaoDocumentoSequencialManager;
import br.com.infox.epp.processo.documento.type.TipoAlteracaoDocumento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@AutoCreate
@Name(DocumentoManager.NAME)
public class DocumentoManager extends Manager<DocumentoDAO, Documento> {

    public static final String NAME = "documentoManager";
    private static final long serialVersionUID = 1L;

    @In
    private DocumentoBinManager documentoBinManager;
    @In
    private HistoricoStatusDocumentoManager historicoStatusDocumentoManager;
    @In
    private PastaManager pastaManager;
    @In
    private ClassificacaoDocumentoManager classificacaoDocumentoManager;
    @In
    private ProcessoManager processoManager;
    @In
    private NumeracaoDocumentoSequencialManager numeracaoDocumentoSequencialManager;
    @In
    private ClassificacaoDocumentoPapelManager classificacaoDocumentoPapelManager;
    @In
    private AssinaturaDocumentoService assinaturaDocumentoService;
    @In
    private PapelManager papelManager;

    public String getModeloDocumentoByIdDocumento(Integer idDocumento) {
        return getDao().getModeloDocumentoByIdDocumento(idDocumento);
    }

    public List<Documento> getDocumentosFromDocumentoBin(DocumentoBin documentoBin){
    	return getDao().getDocumentosFromDocumentoBin(documentoBin);
    }
    
    public String valorDocumento(Integer idDocumento) {
        return find(idDocumento).getDocumentoBin().getModeloDocumento();
    }

    public void exclusaoRestauracaoLogicaDocumento(Documento documento, String motivo, TipoAlteracaoDocumento tipoAlteracaoDocumento)
            throws DAOException {
        this.historicoStatusDocumentoManager.gravarHistoricoDocumento(motivo,
                tipoAlteracaoDocumento, documento);
        if (tipoAlteracaoDocumento == TipoAlteracaoDocumento.E) {
            documento.setExcluido(true);
        } else if (tipoAlteracaoDocumento == TipoAlteracaoDocumento.R) {
            documento.setExcluido(false);
        }
        update(documento);
    }

    public Documento gravarDocumentoNoProcesso(Processo processo, Documento documento) throws DAOException {
        documento.setProcesso(processo);
        documento.setNumeroDocumento(getNextNumeracao(documento));
        documento.setDocumentoBin(this.documentoBinManager.createProcessoDocumentoBin(documento));
        documento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            documento.setIdJbpmTask(idJbpmTask);
        }
        documento.setPasta(pastaManager.getDefaultFolder(processo));
        persist(documento);
        return documento;
    }

    public Documento gravarDocumentoNoProcesso(Processo processo, Documento documento, Pasta pasta) throws DAOException {
        gravarDocumentoNoProcesso(processo, documento);
        if (pasta != null) {
            documento.setPasta(pasta);
            update(documento);
        }
        return documento;
    }
    
    public Documento createDocumento(Processo processo, String label, DocumentoBin bin, ClassificacaoDocumento classificacaoDocumento)
            throws DAOException {
        final Documento doc = new Documento();
        doc.setDocumentoBin(bin);
        doc.setDataInclusao(new Date());
        doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        doc.setProcesso(processo);
        doc.setDescricao(label);
        doc.setExcluido(Boolean.FALSE);
        doc.setPasta(pastaManager.getDefaultFolder(processo));
        // TODO adicionar a classificação de documento na lista de classes
        // gerenciadas antes de entrar aqui
        this.classificacaoDocumentoManager.refresh(classificacaoDocumento);
        doc.setClassificacaoDocumento(classificacaoDocumento);
        doc.setNumeroDocumento(numeracaoDocumentoSequencialManager.getNextNumeracaoDocumentoSequencial(processo));
        return persist(doc);
    }

    public List<Documento> getDocumentoByTask(org.jbpm.taskmgmt.exe.TaskInstance task) {
        return getDao().getDocumentoListByTask(task);
    }

    public Integer getNextNumeracao(Documento documento) throws DAOException {
        return numeracaoDocumentoSequencialManager.getNextNumeracaoDocumentoSequencial(documento.getProcesso());
    }

    public List<Documento> getAnexosPublicos(long idJbpmTask) {
        return getDao().getAnexosPublicos(idJbpmTask);
    }

    public List<Documento> getListDocumentoByProcesso(Processo processo) {
        return getDao().getListDocumentoByProcesso(processo);
    }
    
    public List<Documento> getListDocumentoMinutaByProcesso(Processo processo) {
    	return getDao().getListDocumentoByProcesso(processo);
    }
    
    public int getTotalDocumentosProcesso(Processo processo) {
    	return getDao().getTotalDocumentosProcesso(processo);
    }
    
    public List<Documento> getDocumentosSessaoAnexar(Processo processo, List<Integer> idsDocumentos) {
    	return getDao().getDocumentosSessaoAnexar(processo, idsDocumentos);
    }
    
    @Override
    public Documento persist(Documento o) throws DAOException {
    	o = super.persist(o);
    	atualizarSuficienciaAssinatura(o);
    	return o;
    }
    
    @Override
    public Documento update(Documento o) throws DAOException {
        o = super.update(o);
        atualizarSuficienciaAssinatura(o);
    	return o;
    }
    
    private void atualizarSuficienciaAssinatura(Documento o ) throws DAOException{
        if (!o.getDocumentoBin().getSuficientementeAssinado() && assinaturaDocumentoService.isDocumentoTotalmenteAssinado(o)) {
            assinaturaDocumentoService.setDocumentoSuficientementeAssinado(o.getDocumentoBin(), Authenticator.getUsuarioPerfilAtual());
        }
    }
    
    public boolean isDocumentoInclusoPorHierarquia(Documento documento, String identificadorPapelBase) {
        return isDocumentoInclusoPorPapeis(documento, papelManager.getIdentificadoresPapeisHerdeiros(identificadorPapelBase));
    }

    public boolean isDocumentoInclusoPorPapeis(Documento documento, List<String> identificadoresPapeis) {
        return identificadoresPapeis.contains(documento.getPerfilTemplate().getPapel().getIdentificador());
    }
}
