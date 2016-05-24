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
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.system.Parametros;
import br.com.infox.ibpm.type.PooledActorType;
import br.com.infox.seam.exception.BusinessException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoInternaService {
    
    private static final String DESTINATARIO = "destinatario";
    private static final String EXPEDIDOR = "expedidor";
    public static final String DOCUMENTO_COMUNICACAO = "documentoComunicacao";
    
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
    private IniciarProcessoService iniciarProcessoService;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gravarDestinatario(DestinatarioModeloComunicacao destinatarioModeloComunicacao) {
        entityManager.persist(destinatarioModeloComunicacao);
        entityManager.flush();
        destinatarioModeloComunicacao.getModeloComunicacao().getDestinatarios().add(destinatarioModeloComunicacao);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removerModeloComunicacao(ModeloComunicacao modeloComunicacao) {
        for(DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
            entityManager.remove(destinatario);
        }
        entityManager.remove(modeloComunicacao);
        entityManager.flush();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void enviarComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException, IOException, DocumentException {
        validarEnvioComunicacao(modeloComunicacao);
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        expedirComunicacaoDestinatarios(modeloComunicacao);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);
    }
    
    private void validarEnvioComunicacao(ModeloComunicacao modeloComunicacao) {
        if (modeloComunicacao.isMinuta()) {
            throw new BusinessException("Documento em elaboração não pode ser enviado!");
        }
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
        entityManager.merge(modeloComunicacao);
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
        pastaManager.createDefaultFoldersChild(processo);
        
        Documento documentoComunicacao = criarDocumentoComunicacao(processo, modeloComunicacao);
        
        Map<String, Object> variables = createVariables(modeloComunicacao, documentoComunicacao);
        
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        BusinessProcess.instance().setProcessId(null);
        BusinessProcess.instance().setTaskId(null);
        iniciarProcessoService.iniciarProcesso(processo, variables, false);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);

        for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
            destinatario.setProcesso(processo);
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
        pastaManager.createDefaultFoldersChild(processo);
        
        Documento documentoComunicacao = criarDocumentoComunicacao(processo, destinatarioModeloComunicacao.getModeloComunicacao());
        
        Map<String, Object> variables = createVariables(destinatarioModeloComunicacao, documentoComunicacao);
        
        Long processIdOriginal = BusinessProcess.instance().getProcessId();
        Long taskIdOriginal = BusinessProcess.instance().getTaskId();
        BusinessProcess.instance().setProcessId(null);
        BusinessProcess.instance().setTaskId(null);
        iniciarProcessoService.iniciarProcesso(processo, variables, false);
        BusinessProcess.instance().setProcessId(processIdOriginal);
        BusinessProcess.instance().setTaskId(taskIdOriginal);

        destinatarioModeloComunicacao.setProcesso(processo);
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
        documentoBin = documentoBinManager.createProcessoDocumentoBin(documentoBin);
        return documentoManager.createDocumento(processo, "Comunicação Interna", documentoBin, modeloComunicacao.getClassificacaoComunicacao());
    }

    private Map<String, Object> createVariables(ModeloComunicacao modeloComunicacao, Documento documento) {
        Map<String, Object> variables = new HashMap<String, Object>();
        
        List<String> destinatarios = new ArrayList<>();
        for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
            if (destinatario.getDestinatario() != null) {
                destinatarios.add(PooledActorType.USER.toPooledActorId(destinatario.getDestinatario().getUsuarioLogin().getLogin()));
            } else if (destinatario.getPerfilDestino() != null && destinatario.getDestino() != null) {
                destinatarios.add(PooledActorType.GROUP.toPooledActorId(destinatario.getDestino().getCodigo()+"&"+destinatario.getPerfilDestino().getId()));
            } else {
                destinatarios.add(PooledActorType.LOCAL.toPooledActorId(destinatario.getDestino().getCodigo()));
            }
        }
        variables.put(DESTINATARIO, StringUtil.concatList(destinatarios, ","));
        variables.put(EXPEDIDOR, PooledActorType.USER.toPooledActorId(Authenticator.getUsuarioLogado().getLogin()));
        variables.put(DOCUMENTO_COMUNICACAO, documento.getId());
        return variables;
    }
    
    private Map<String, Object> createVariables(DestinatarioModeloComunicacao destinatario, Documento documento) {
        Map<String, Object> variables = new HashMap<String, Object>();
        
        String destinatarioVariable = "";
        if (destinatario.getDestinatario() != null) {
            destinatarioVariable = PooledActorType.USER.toPooledActorId(destinatario.getDestinatario().getUsuarioLogin().getLogin());
        } else if (destinatario.getPerfilDestino() != null && destinatario.getDestino() != null) {
            destinatarioVariable = PooledActorType.GROUP.toPooledActorId(destinatario.getDestino().getCodigo()+"&"+destinatario.getPerfilDestino().getId());
        } else {
            destinatarioVariable = PooledActorType.LOCAL.toPooledActorId(destinatario.getDestino().getCodigo());
        }
        variables.put(DESTINATARIO, destinatarioVariable);
        variables.put(EXPEDIDOR, PooledActorType.USER.toPooledActorId(Authenticator.getUsuarioLogado().getLogin()));
        variables.put(DOCUMENTO_COMUNICACAO, documento.getId());
        return variables;
    }
    
    private NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() throws DAOException {
        Fluxo fluxo = fluxoManager.getFluxoByCodigo(Parametros.CODIGO_FLUXO_COMUNICACAO_INTERNA.getValue());
        List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
        return ncfs.get(0);
    }
    
}
