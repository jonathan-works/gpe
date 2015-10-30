package br.com.infox.epp.processo.documento.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.bpm.TaskInstance;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.PermissaoService;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.manager.ClassificacaoDocumentoManager;
import br.com.infox.epp.processo.documento.dao.DocumentoTemporarioDao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.DocumentoTemporario;
import br.com.infox.epp.processo.documento.numeration.NumeracaoDocumentoSequencialManager;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentoTemporarioManager {
    
    private static final String RECURSO_ANEXAR_DOCUMENTO_SEM_ANALISE = "anexarDocumentoSemAnalise";
    private NumeracaoDocumentoSequencialManager numeracaoDocumentoSequencialManager = ComponentUtil.getComponent(NumeracaoDocumentoSequencialManager.NAME);
    private DocumentoBinManager documentoBinManager = ComponentUtil.getComponent(DocumentoBinManager.NAME);
    private PastaManager pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
    private PermissaoService permissaoService = ComponentUtil.getComponent(PermissaoService.NAME);
    private DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
    private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService = ComponentUtil.getComponent(ProcessoAnaliseDocumentoService.NAME);
    private ClassificacaoDocumentoManager classificacaoDocumentoManager = ComponentUtil.getComponent(ClassificacaoDocumentoManager.NAME);
    
    @Inject
    private DocumentoTemporarioDao documentoTemporarioDao;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravarDocumentoTemporario(DocumentoTemporario documentoTemporario, byte[] data) throws DAOException {
        documentoTemporario.getDocumentoBin().setProcessoDocumento(data);
        gravarDocumentoTemporario(documentoTemporario);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravarDocumentoTemporario(DocumentoTemporario documentoTemporario) throws DAOException {
    	documentoTemporario.setDocumentoBin(documentoBinManager.createProcessoDocumentoBin(documentoTemporario.getDocumentoBin()));
    	if (TaskInstance.instance() != null) {
    		documentoTemporario.setIdJbpmTask(TaskInstance.instance().getId());
    	}
    	if (documentoTemporario.getPasta() == null) {
    		documentoTemporario.setPasta(pastaManager.getDefault(documentoTemporario.getProcesso()));
    	}
    	documentoTemporarioDao.persist(documentoTemporario);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DocumentoTemporario update(DocumentoTemporario documentoTemporario) throws DAOException {
    	documentoTemporario.setUsuarioAlteracao(Authenticator.getUsuarioLogado());
    	documentoTemporario.setDataAlteracao(new Date());
    	documentoBinManager.update(documentoTemporario.getDocumentoBin());
    	return documentoTemporarioDao.update(documentoTemporario);
    }

    public List<DocumentoTemporario> listByProcesso(Processo processo, Localizacao localizacao, String order) {
        return documentoTemporarioDao.listByProcesso(processo, localizacao, order);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeAll(List<DocumentoTemporario> documentoTemporarioList) throws DAOException {
		documentoTemporarioDao.removeAll(documentoTemporarioList);
	}

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void transformarEmDocumento(List<DocumentoTemporario> documentosParaEnviar) throws DAOException {
        List<Documento> documentosParaAnalise = new ArrayList<>();
        
        for (DocumentoTemporario documentoTemporario : documentosParaEnviar) {
            Documento documentoCriado = createDocumento(documentoTemporario);
            documentoManager.persist(documentoCriado);
            Papel papelInclusao = documentoTemporario.getPerfilTemplate().getPapel();
            if (!permissaoService.papelPossuiPermissaoParaRecurso(papelInclusao, RECURSO_ANEXAR_DOCUMENTO_SEM_ANALISE)) {
                documentosParaAnalise.add(documentoCriado);
            }
        }
        
        if (!documentosParaAnalise.isEmpty()) {
            Processo processo = documentosParaAnalise.get(0).getProcesso();
            Documento[] arrayDocumentos = documentosParaAnalise.toArray(new Documento[documentosParaAnalise.size()]);
            Processo processoAnaliseDoc = processoAnaliseDocumentoService.criarProcessoAnaliseDocumentos(processo, arrayDocumentos);
            processoAnaliseDocumentoService.inicializarFluxoDocumento(processoAnaliseDoc, null);
        }
    }

    private Documento createDocumento(DocumentoTemporario dt) throws DAOException {
        ClassificacaoDocumento classificacaoDocumento = classificacaoDocumentoManager.find(dt.getClassificacaoDocumento().getId());
        DocumentoBin docBin = documentoBinManager.find(dt.getDocumentoBin().getId());
        Documento documento = new Documento();
        documento.setClassificacaoDocumento(classificacaoDocumento);
        documento.setDocumentoBin(docBin);
        documento.setProcesso(dt.getProcesso());
        documento.setDescricao(dt.getDescricao());
        documento.setNumeroDocumento(numeracaoDocumentoSequencialManager.getNextNumeracaoDocumentoSequencial(dt.getProcesso()));
        documento.setAnexo(dt.getAnexo());
        documento.setIdJbpmTask(dt.getIdJbpmTask());
        documento.setPerfilTemplate(dt.getPerfilTemplate());
        documento.setDataInclusao(dt.getDataInclusao());
        documento.setUsuarioInclusao(dt.getUsuarioInclusao());
        documento.setDataAlteracao(dt.getDataAlteracao());
        documento.setUsuarioAlteracao(dt.getUsuarioAlteracao());
        documento.setExcluido(Boolean.FALSE);
        documento.setPasta(dt.getPasta());
        documento.setLocalizacao(dt.getLocalizacao());
        return documento;
    }
    
    public DocumentoTemporario loadById(Integer id) {
        return documentoTemporarioDao.loadById(id);
    }
}