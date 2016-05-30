package br.com.infox.epp.processo.documento.manager;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.TaskInstance;
import org.joda.time.DateTime;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioPerfil;
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
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
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
        documento.setDocumentoBin(this.documentoBinManager.createProcessoDocumentoBin(documento));
        if (documento.getUsuarioInclusao() == null) {
        	documento.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        }
        if (documento.getDataInclusao() == null) {
        	documento.setDataInclusao(new Date());
        }
        if (TaskInstance.instance() != null) {
            long idJbpmTask = TaskInstance.instance().getId();
            documento.setIdJbpmTask(idJbpmTask);
        }
        if (processo != null) {
        	if (documento.getPasta() == null) {
        		documento.setPasta(pastaManager.getDefaultFolder(processo));
        	} else if (!processo.equals(documento.getPasta().getProcesso())) {
        		throw new BusinessRollbackException("O processo informado e o processo da pasta do documento são diferentes");
        	}
        }
        documento.setNumeroDocumento(getNextNumeracao(documento));
        persist(documento);
        return documento;
    }
    
    public Documento gravarDocumentoNoProcesso(Documento documento) throws DAOException {
    	return gravarDocumentoNoProcesso(null, documento);
    }
    
    public Documento createDocumento(Processo processo, String label, DocumentoBin bin, ClassificacaoDocumento classificacaoDocumento)
            throws DAOException {
        final Documento doc = new Documento();
        doc.setDocumentoBin(bin);
        doc.setDataInclusao(new Date());
        doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        doc.setDescricao(label);
        doc.setExcluido(Boolean.FALSE);
        doc.setPasta(pastaManager.getDefaultFolder(processo));
        doc.setClassificacaoDocumento(classificacaoDocumentoManager.getReference(classificacaoDocumento.getId()));
        doc.setNumeroDocumento(numeracaoDocumentoSequencialManager.getNextNumeracaoDocumentoSequencial(processo));
        return persist(doc);
    }

    public List<Documento> getDocumentoByTask(org.jbpm.taskmgmt.exe.TaskInstance task) {
        return getDao().getDocumentoListByTask(task);
    }

    public Integer getNextNumeracao(Documento documento) throws DAOException {
        return numeracaoDocumentoSequencialManager.getNextNumeracaoDocumentoSequencial(documento.getPasta().getProcesso());
    }

    public List<Documento> getAnexosPublicos(long idJbpmTask) {
        return getDao().getAnexosPublicos(idJbpmTask);
    }

    public List<Documento> getListDocumentoByProcesso(Processo processo) {
        return getDao().getListDocumentoByProcesso(processo);
    }
    
    public List<Documento> getListAllDocumentoByProcesso(Processo processo) {
        return getDao().getListAllDocumentoByProcesso(processo);
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
    	return papelManager.isPapelHerdeiro(documento.getPerfilTemplate().getPapel().getIdentificador(), identificadorPapelBase);
    }

    public boolean isDocumentoInclusoPorPapeis(Documento documento, List<String> identificadoresPapeis) {
        return identificadoresPapeis.contains(documento.getPerfilTemplate().getPapel().getIdentificador());
    }
    
    /**
     * Define se um papel deve assinar um documento (checando se sua asinatura é obrigatória e se não existem outras restrições 
     * para que o papel possa ser assinado, como processo finalizado e assinatura já feita pelo papel) 
     */
    public boolean deveAssinar(Documento documento, Papel papel) {
        	return documento.isDocumentoAssinavel(papel) && documento.isAssinaturaObrigatoria(papel) && !documento.isDocumentoAssinado(papel);
    }
    
    /**
     * Diz se um usuário pode assinar um documento (verificando se o documento já foi assinado por esse papel)
     */
    public boolean podeAssinar(Documento documento, UsuarioPerfil usuarioPerfil) {
		if (documento == null || !usuarioPerfil.getAtivo())
			return false;

		boolean isAssinavel = documento.isDocumentoAssinavel(usuarioPerfil.getPerfilTemplate().getPapel());
		if (!isAssinavel)
			return false;

		boolean assinadoPor = assinaturaDocumentoService.isDocumentoAssinado(documento, usuarioPerfil);

		return isAssinavel && !assinadoPor;
    }
    
	public Documento copiarDocumento(Documento original, Pasta novaPasta) throws CloneNotSupportedException {
		Documento cDoc = original.makeCopy();
        cDoc.setPasta(novaPasta);
        cDoc.setNumeroDocumento(null);
        cDoc.setDataInclusao(DateTime.now().toDate());
        cDoc.setUsuarioInclusao(null);
        return persist(cDoc);
	}
}
