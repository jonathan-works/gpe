package br.com.infox.epp.processo.comunicacao.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jboss.seam.ScopeType;
import org.jboss.seam.bpm.BusinessProcess;
import org.joda.time.DateTime;

import com.lowagie.text.DocumentException;

import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.numeration.NumeracaoDocumentoSequencialManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoInternaService {
    
    private static final String DESTINATARIO = "destinatario";
    private static final String EXPEDIDOR = "expedidor";
    private static final String DOCUMENTO_COMUNICACAO = "documentoComunicacao";
    
    @Inject
    private EntityManager entityManager;
    @Inject
    private ProcessoManager processoManager;
    @Inject
    private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
    @Inject
    private FluxoManager fluxoManager;
    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private DocumentoBinManager documentoBinManager;
    @Inject
    private PastaManager pastaManager;
    @Inject
    private PdfManager pdfManager;
    @Inject
    private NumeracaoDocumentoSequencialManager numeracaoDocumentoSequencialManager;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravarDestinatario(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
        entityManager.persist(destinatarioModeloComunicacao);
        entityManager.flush();
        destinatarioModeloComunicacao.getModeloComunicacao().getDestinatarios().add(destinatarioModeloComunicacao);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void enviarComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException, IOException, DocumentException {
        validarEnvioComunicacao();
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        expedirComunicacaoDestinatarios(modeloComunicacao);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);
    }
    
    private void validarEnvioComunicacao() {
        String codigoFluxoComunicacao = Parametros.CODIGO_FLUXO_COMUNICACAO_INTERNA.getValue();
        if (StringUtil.isEmpty(codigoFluxoComunicacao)) {
            throw new BusinessException("Paramêtro '" + Parametros.CODIGO_FLUXO_COMUNICACAO_INTERNA.getLabel() + "' não configurado!");
        }
        Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoComunicacao);
        if (fluxo == null || !fluxo.getPublicado() ) {
            throw new BusinessException("Fluxo com código '" + codigoFluxoComunicacao + "' indisponível!");
        }
        List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
        if (ncfs.isEmpty()) {
            throw new BusinessException("Natureza/Categoria/Fluxo não cadastrado para FLuxo " + fluxo.getFluxo());
        }
    }
    
    private void expedirComunicacaoDestinatarios(ModeloComunicacao modeloComunicacao) throws DAOException, IOException, DocumentException {
        List<DestinatarioModeloComunicacao> destinatariosIndividuais = new ArrayList<>();
        for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
            if (destinatario.getIndividual()) {
                destinatariosIndividuais.add(destinatario);
            }
        }
        modeloComunicacao.getDestinatarios().removeAll(destinatariosIndividuais);
        
        if (!modeloComunicacao.getDestinatarios().isEmpty()) {
            expedirComunicacao(modeloComunicacao);
        }
        
        for (DestinatarioModeloComunicacao destinatarioIndividual : destinatariosIndividuais) {
            expedirComunicacao(destinatarioIndividual);
            modeloComunicacao.getDestinatarios().add(destinatarioIndividual);
        }
        
        modeloComunicacao.setFinalizada(true);
        entityManager.flush();
    }

    private void expedirComunicacao(ModeloComunicacao modeloComunicacao) throws IOException, DocumentException {
        Processo processo = new Processo();
        processo.setLocalizacao(Authenticator.getLocalizacaoAtual());
        processo.setNaturezaCategoriaFluxo(getNaturezaCategoriaFluxo());
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setProcessoPai(modeloComunicacao.getProcesso());
        processo.setDataInicio(DateTime.now().toDate());
        processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
        processoManager.persist(processo);
        pastaManager.createDefaultFolders(processo);
        
        Documento documentoComunicacao = criarDocumentoComunicacao(processo, modeloComunicacao);
        
        Map<String, Object> variables = createVariables(modeloComunicacao, documentoComunicacao);
        
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        BusinessProcess.instance().setProcessId(null);
        BusinessProcess.instance().setTaskId(null);
        getIniciarProcessoService().iniciarProcesso(processo, variables);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);

        for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
            destinatario.setExpedido(true);
            destinatario.setDocumentoComunicacao(documentoComunicacao);
            entityManager.merge(destinatario);
        }
    }
    
    private void expedirComunicacao(DestinatarioModeloComunicacao destinatarioModeloComunicacao) throws DAOException, IOException, DocumentException {
        Processo processo = new Processo();
        processo.setLocalizacao(Authenticator.getLocalizacaoAtual());
        processo.setNaturezaCategoriaFluxo(getNaturezaCategoriaFluxo());
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setProcessoPai(destinatarioModeloComunicacao.getModeloComunicacao().getProcesso());
        processo.setDataInicio(DateTime.now().toDate());
        processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
        processoManager.persist(processo);
        pastaManager.createDefaultFolders(processo);
        
        Documento documentoComunicacao = criarDocumentoComunicacao(processo, destinatarioModeloComunicacao.getModeloComunicacao());
        
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        BusinessProcess.instance().setProcessId(null);
        BusinessProcess.instance().setTaskId(null);
        getIniciarProcessoService().iniciarProcesso(processo);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);

//        criarMetadados(destinatarioModeloComunicacao, processo);
        
       destinatarioModeloComunicacao.setExpedido(true);
       destinatarioModeloComunicacao.setDocumentoComunicacao(documentoComunicacao);
       entityManager.merge(destinatarioModeloComunicacao);
       entityManager.flush();
    }
    
    private Documento criarDocumentoComunicacao(Processo processo, ModeloComunicacao modeloComunicacao) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String textoEditor = modeloComunicacao.getTextoComunicacao() == null ? "" : modeloComunicacao.getTextoComunicacao();
        pdfManager.convertHtmlToPdf(textoEditor, outputStream);
        DocumentoBin documentoBin = documentoBinManager.createProcessoDocumentoBin("Comunicação Interna", outputStream.toByteArray(), "pdf");
        documentoBinManager.createProcessoDocumentoBin(documentoBin);
        Documento doc = new Documento();
        doc.setDocumentoBin(documentoBin);
        doc.setDataInclusao(DateTime.now().toDate());
        doc.setUsuarioInclusao(Authenticator.getUsuarioLogado());
        doc.setProcesso(processo);
        doc.setDescricao("Comunicação Interna");
        doc.setExcluido(Boolean.FALSE);
        doc.setPasta(pastaManager.getDefaultFolder(processo));
        doc.setClassificacaoDocumento(modeloComunicacao.getClassificacaoComunicacao());
        doc.setNumeroDocumento(numeracaoDocumentoSequencialManager.getNextNumeracaoDocumentoSequencial(processo));
        return documentoManager.persist(doc);
    }

//    private void criarMetadados(ModeloComunicacao modeloComunicacao, Processo processo) throws DAOException {
//        MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
//        List<MetadadoProcesso> metadados = new ArrayList<>();
//
//        metadados.add(metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.MEIO_EXPEDICAO, MeioExpedicao.SI.name()));
//        
//        metadadoProcessoManager.persistMetadados(metadadoProcessoProvider, metadados);
//    }
//    
//    private void criarMetadados(DestinatarioModeloComunicacao destinatarioModeloComunicacao, Processo processo) throws DAOException {
//        MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
//        List<MetadadoProcesso> metadados = new ArrayList<>();
//        
//        metadados.add(metadadoProcessoProvider.gerarMetadado(ComunicacaoMetadadoProvider.MEIO_EXPEDICAO, MeioExpedicao.SI.name()));
//        
//        metadadoProcessoManager.persistMetadados(metadadoProcessoProvider, metadados);
//    }
    
    private Map<String, Object> createVariables(ModeloComunicacao modeloComunicacao, Documento documento) {
        Map<String, Object> variables = new HashMap<String, Object>();
        
        List<String> destinatarios = new ArrayList<>();
        for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
            if (destinatario.getDestinatario() != null) {
                destinatarios.add(destinatario.getDestinatario().getUsuarioLogin().getLogin());
            } else if (destinatario.getPerfilDestino() != null && destinatario.getDestino() != null) {
                destinatarios.add(destinatario.getDestino().getCodigo()+":"+destinatario.getPerfilDestino().getId());
            } else {
                destinatarios.add(destinatario.getDestino().getCodigo());
            }
        }
        variables.put(DESTINATARIO, StringUtil.concatList(destinatarios, ","));
        variables.put(EXPEDIDOR, Authenticator.getUsuarioLogado().getLogin());
        variables.put(DOCUMENTO_COMUNICACAO, documento.getId().longValue());
        return variables;
    }
    
    private NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() throws DAOException {
        Fluxo fluxo = fluxoManager.getFluxoByCodigo(Parametros.CODIGO_FLUXO_COMUNICACAO_INTERNA.getValue());
        List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
        return ncfs.get(0);
    }
    
    private IniciarProcessoService getIniciarProcessoService() {
        return ComponentUtil.getComponent(IniciarProcessoService.NAME, ScopeType.CONVERSATION);
    }
    
}
