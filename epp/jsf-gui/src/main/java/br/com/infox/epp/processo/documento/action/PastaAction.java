package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.FacesMessages;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.list.DataList;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumento;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.list.DocumentoCompartilhamentoList;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.manager.AssinaturaDocumentoManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class PastaAction implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private PastaManager pastaManager;
    @Inject
    private ActionMessagesService actionMessagesService;
    @Inject
    private DocumentoManager documentoManager;
    @Inject
    private DocumentoCompartilhamentoList documentoCompartilhamentoList;
    @Inject
    private DocumentoCompartilhamentoSearch documentoCompartilhamentoSearch;
    @Inject
    private DocumentoCompartilhamentoService documentoCompartilhamentoService;
    @Inject
    private DocumentoCompartilhamentoView documentoCompartilhamentoView;
    @Inject
    private PastaRestricaoManager pastaRestricaoManager;
    @Inject
    private PastaCompartilhamentoSearch pastaCompartilhamentoSearch;
    @Inject
    private PastaCompartilhamentoService pastaCompartilhamentoService;
    @Inject
    private PastaCompartilhamentoView pastaCompartilhamentoView;
    @Inject
    private DocumentoProcessoAction documentoProcessoAction;
    @Inject
	private AssinaturaDocumentoManager assinaturaDocumentoManager;
    
    private DocumentoList documentoList = ComponentUtil.getComponent(DocumentoList.NAME);

    private LogProvider LOG = Logging.getLogProvider(PastaAction.class);

    private Processo processo;
    private List<Pasta> pastaList = new ArrayList<>();
    private Pasta instance;
    private Integer id;
    private Map<Integer, PastaRestricaoBean> restricoes;
    private Boolean showGrid = false;

    private Map<Integer, Boolean> possuiCompartilhamentoMap = new HashMap<>();
    private Map<Integer, Boolean> possuiCompartilhamentoDocMap = new HashMap<>();
    private List<Pasta> pastaCompartilhadaList = new ArrayList<>(0);
    private List<Pasta> pastaDocCompartilhamentoList = new ArrayList<>(0);
    private List<Documento> documentoCompartilhadoList = new ArrayList<>(0);
    private List<PastaCompartilhamentoProcessoVO> processoPastaCompList;
    private Pasta compartilhamentoToRemove;
    private Documento compartilhamentoDocumentoToRemove;
    private boolean documentoCompartilhado = false;
    private boolean pastaCompartilhada = false;

    private String msgConfigurarCompartilhamento;
    private String msgConfigurarCompartilhamentoDoc;
    private String msgRemoverCompartilhamento;
    private String msgRemoverCompartilhamentoDocumento;
    private String nomePastaDocumentosCompartilhados;

    @PostConstruct
    public void create() {
        newInstance();
        InfoxMessages infoxMessages = InfoxMessages.getInstance();
        msgConfigurarCompartilhamento = infoxMessages.get("pasta.compartilhamento.msgConfigurar");
        msgConfigurarCompartilhamentoDoc = infoxMessages.get("documento.compartilhamento.msgConfigurar");
        msgRemoverCompartilhamento = infoxMessages.get("pasta.compartilhamento.remover.msg");
        msgRemoverCompartilhamentoDocumento = infoxMessages.get("documento.compartilhamento.remover.msg");
        nomePastaDocumentosCompartilhados = infoxMessages.get("documento.compartilhamento.nomePasta");
    }

    public void newInstance() {
        setInstance(new Pasta());
        setRestricoes(new HashMap<Integer, PastaRestricaoBean>());
    }

    private void changeSelectedPasta(Pasta pasta) {
        documentoList.selectPasta(pasta);
        setInstance(pasta);
        setShowGrid(true);
        setDocumentoCompartilhado(false);
        for (Documento documento : documentoList.list(15)) {
            documentoProcessoAction.deveMostrarCadeado(documento);
        }
    }

    public void selectPasta(Pasta pasta) {
        changeSelectedPasta(pasta);
        setPastaCompartilhada(false);
    }

    public void selectPastaCompartilhada(Pasta pasta) {
        changeSelectedPasta(pasta);
        setPastaCompartilhada(true);
    }

    public void selectPastaDocumentosCompartilhados(Processo processo) {
        documentoCompartilhamentoList.setProcesso(processo);
        documentoCompartilhamentoList.refresh();
        setInstance(null);
        setShowGrid(true);
        setDocumentoCompartilhado(true);
    }

    public void associaDocumento(DropEvent evt) {
        Object od = evt.getDragValue();
        Object op = evt.getDropValue();
        if (od instanceof Documento && op instanceof Pasta) {
            Documento doc = (Documento) od;
            Pasta pasta = (Pasta) op;
            Pasta pastaAnterior = pastaManager.find(doc.getPasta().getId());
            if (pastaAnterior.equals(pasta)) return;
            try {
                doc.setPasta(pasta);
                documentoManager.update(doc);
                pastaManager.refresh(pasta);
                pastaManager.refresh(pastaAnterior);
                documentoList.refresh();
            } catch (DAOException e) {
                actionMessagesService.handleDAOException(e);
            }
        }
    }

    public Boolean canSee(Pasta pasta) {
        PastaRestricaoBean restricaoDaPasta = restricoes.get(pasta.getId());
        return restricaoDaPasta != null && restricaoDaPasta.getRead();
    }

    public Boolean canWrite(Pasta pasta) {
        PastaRestricaoBean restricaoDaPasta = restricoes.get(pasta.getId());
        return restricaoDaPasta != null && restricaoDaPasta.getWrite();
    }
    
    public Boolean canDelete(Pasta pasta) {
        if (documentoCompartilhado) return false;
        PastaRestricaoBean restricaoDaPasta = restricoes.get(pasta.getId());
        return restricaoDaPasta != null && restricaoDaPasta.getDelete();
    }
    
    public Boolean canDeleteFromInstance() {
        return canDelete(getInstance());
    }
    
    public Boolean canLogicDelete(Pasta pasta) {
        if (documentoCompartilhado) return false;
        PastaRestricaoBean restricaoDaPasta = restricoes.get(pasta.getId());
        return restricaoDaPasta != null && restricaoDaPasta.getLogicDelete();
    }

    public Boolean canLogicDeleteFromInstance() {
        return canLogicDelete(getInstance());
    }

    public boolean possuiCompartilhamento(Pasta pasta) {
        Boolean possuiCompartilhamento = possuiCompartilhamentoMap.get(pasta.getId());
        if (possuiCompartilhamento == null) {
            possuiCompartilhamento = pastaCompartilhamentoSearch.possuiCompartilhamento(pasta);
            possuiCompartilhamentoMap.put(pasta.getId(), possuiCompartilhamento);
        }
        return possuiCompartilhamento;
    }

    public boolean possuiCompartilhamento(Documento documento) {
        Boolean possuiCompartilhamento = possuiCompartilhamentoDocMap.get(documento.getId());
        if (possuiCompartilhamento == null) {
            possuiCompartilhamento = documentoCompartilhamentoSearch.possuiCompartilhamento(documento);
            possuiCompartilhamentoDocMap.put(documento.getId(), possuiCompartilhamento);
        }
        return possuiCompartilhamento;
    }

    public Pasta getInstance() {
        return instance;
    }

    public void setInstance(Pasta pasta) {
        this.instance = pasta;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo.getProcessoRoot();
        UsuarioLogin usuario = Authenticator.getUsuarioLogado();
        Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        Papel papel = Authenticator.getPapelAtual();
        try {
            this.pastaList = pastaManager.getByProcesso(processo.getProcessoRoot());
            this.restricoes = pastaRestricaoManager.loadRestricoes(processo.getProcessoRoot(), usuario, localizacao, papel);

            if (!processo.equals(processo.getProcessoRoot())) {
                this.pastaList.addAll(pastaManager.getByProcesso(processo));
                this.restricoes.putAll(pastaRestricaoManager.loadRestricoes(processo, usuario, localizacao, papel));
            }

            loadDadosCompartilhamento(usuario, localizacao, papel);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    private void loadDadosCompartilhamento(UsuarioLogin usuario, Localizacao localizacao, Papel papel) {
        loadPastasCompartilhadas(usuario, localizacao, papel);
        loadDocumentosCompartilhados(usuario, localizacao, papel);
        loadVOPastasCompartilhadas();
    }

    private void loadPastasCompartilhadas(UsuarioLogin usuario, Localizacao localizacao, Papel papel) {
        this.pastaCompartilhadaList = pastaCompartilhamentoSearch.listPastasCompartilhadas(processo);
        Map<Integer, PastaRestricaoBean> map = pastaRestricaoManager.loadRestricoes(pastaCompartilhadaList, usuario, localizacao, papel);
        for (PastaRestricaoBean restricaoBean : map.values()) {
            restricaoBean.setDelete(false);
            restricaoBean.setLogicDelete(false);
            restricaoBean.setWrite(false);
        }
        this.restricoes.putAll(map);
    }

    private void loadDocumentosCompartilhados(UsuarioLogin usuario, Localizacao localizacao, Papel papel) {
        pastaDocCompartilhamentoList = new ArrayList<>(1);
        documentoCompartilhadoList = documentoCompartilhamentoSearch.listDocumentosCompartilhados(processo);
        List<Pasta> pastas = new ArrayList<>(1);
        for (Documento documento : documentoCompartilhadoList) {
            if (!pastas.contains(documento.getPasta())) {
                pastas.add(documento.getPasta());
            }
        }
        Map<Integer, PastaRestricaoBean> map = pastaRestricaoManager.loadRestricoes(pastas, usuario, localizacao, papel);
        for (Pasta pasta : pastas) {
            PastaRestricaoBean restricaoDaPasta = map.get(pasta.getId());
            if (restricaoDaPasta != null && restricaoDaPasta.getRead()) {
                pastaDocCompartilhamentoList.add(pasta);
            }
        }
        documentoCompartilhamentoList.setPastas(pastaDocCompartilhamentoList);
        restricoes.putAll(map);
    }

    private void loadVOPastasCompartilhadas() {
        processoPastaCompList = new ArrayList<>(0);
        Map<String, PastaCompartilhamentoProcessoVO> map = new HashMap<>();
        for (Pasta pasta : pastaCompartilhadaList) {
            if (canSee(pasta)) {
                String numeroProcesso = pasta.getProcesso().getNumeroProcessoRoot();
                PastaCompartilhamentoProcessoVO processoVO = map.get(numeroProcesso);
                if (processoVO != null) {
                    processoVO.addPasta(pasta);
                } else {
                    processoVO = new PastaCompartilhamentoProcessoVO(pasta.getProcesso(), pasta);
                    processoPastaCompList.add(processoVO);
                    map.put(numeroProcesso, processoVO);
                }
            }
        }
        for (Pasta pasta : pastaDocCompartilhamentoList) {
            String numeroProcesso = pasta.getProcesso().getNumeroProcessoRoot();
            PastaCompartilhamentoProcessoVO processoVO = map.get(numeroProcesso);
            if (processoVO != null) {
                processoVO.setDocumentoCompartilhado(true);
            } else {
                processoVO = new PastaCompartilhamentoProcessoVO(pasta.getProcesso(), true);
                processoPastaCompList.add(processoVO);
                map.put(numeroProcesso, processoVO);
            }
        }
    }

    public List<Pasta> getPastaList() {
        return pastaList;
    }

    public List<Pasta> getPastaCompartilhadaList() {
        return pastaCompartilhadaList;
    }

    public List<Pasta> getPastaListComRestricaoEscrita() {
        List<Pasta> pastasRestritas= new ArrayList<>();
        for (Pasta pasta : pastaList) {
            if (canWrite(pasta)) {
                pastasRestritas.add(pasta);
            }
        }
        return pastasRestritas;
    }

    public void configurarCompartilhamentoPasta(Pasta pasta) {
        pastaCompartilhamentoView.initWithPasta(pasta);
        possuiCompartilhamentoMap.remove(pasta.getId());
    }

    public void selectCompartilhamentoToRemove(Pasta pasta) {
        compartilhamentoToRemove = pasta;
    }

    public void selectCompartilhamentoDocumentoToRemove(Documento documento) {
        compartilhamentoDocumentoToRemove = documento;
    }

    public void configurarCompartilhamentoDocumento(Documento documento) {
        documentoCompartilhamentoView.initWithDocumento(documento);
        possuiCompartilhamentoDocMap.remove(documento.getId());
    }

    public String getMsgRemoverCompartilhamento() {
        return compartilhamentoToRemove == null ? ""
                : String.format(msgRemoverCompartilhamento,
                        compartilhamentoToRemove.getNome(),
                        compartilhamentoToRemove.getProcesso().getNumeroProcessoRoot());
    }

    public String getMsgRemoverCompartilhamentoDocumento() {
        if (compartilhamentoDocumentoToRemove == null) return "";
        String descricaoDocumento = compartilhamentoDocumentoToRemove.getDescricao();
        String descricao = descricaoDocumento != null && !descricaoDocumento.isEmpty() && !descricaoDocumento.equals("-")
                ? descricaoDocumento : compartilhamentoDocumentoToRemove.getClassificacaoDocumento().getDescricao();
        return String.format(msgRemoverCompartilhamentoDocumento,
                        compartilhamentoDocumentoToRemove.getNumeroSequencialDocumento().toString(),
                        descricao,
                        compartilhamentoDocumentoToRemove.getPasta().getProcesso().getNumeroProcessoRoot());
    }

    public void removerCompartilhamento() {
        try {
            pastaCompartilhamentoService.removerCompartilhamento(compartilhamentoToRemove, processo, Authenticator.getUsuarioLogado());
            loadDadosCompartilhamento(Authenticator.getUsuarioLogado(), Authenticator.getLocalizacaoAtual(), Authenticator.getPapelAtual());
            if (getInstance().equals(compartilhamentoToRemove)) {
                setShowGrid(false);
            }
            documentoProcessoAction.setListClassificacaoDocumento(null);
            FacesMessages.instance().add("Compartilhamento removido com sucesso.");
        } catch (Exception e) {
            FacesMessages.instance().add("Falha ao tentar remover compartilhamento. Favor tentar novamente.");
            LOG.error("pastaAction.removerCompartilhamento.", e);
        }
    }

    public void removerCompartilhamentoDocumento() {
        try {
            documentoCompartilhamentoService.removerCompartilhamento(compartilhamentoDocumentoToRemove, processo, Authenticator.getUsuarioLogado());
            loadDadosCompartilhamento(Authenticator.getUsuarioLogado(), Authenticator.getLocalizacaoAtual(), Authenticator.getPapelAtual());
            setShowGrid(false);
            documentoProcessoAction.setListClassificacaoDocumento(null);
            for (PastaCompartilhamentoProcessoVO processoVO : processoPastaCompList) {
                if (compartilhamentoDocumentoToRemove.getPasta().getProcesso().getNumeroProcessoRoot().equals(processoVO.getNumeroProcesso())) {
                    if (processoVO.isDocumentoCompartilhado()) {
                        setShowGrid(true);
                        getActiveBean().refresh();
                    }
                }
            }
            FacesMessages.instance().add("Compartilhamento removido com sucesso.");
        } catch (Exception e) {
            FacesMessages.instance().add("Falha ao tentar remover compartilhamento. Favor tentar novamente", e);
            LOG.error("pastaAction.removerCompartilhamentoDocumento", e);
        }
    }

    public void resetarContadoresDocumentosCompartilhados() {
        for (PastaCompartilhamentoProcessoVO processoVO : processoPastaCompList) {
            processoVO.setQtdDocumentoCompartilhado(null);
        }
    }

    public Long getQtdDocumentoCompartilhado(PastaCompartilhamentoProcessoVO processoVO) {
        if (processoVO.getQtdDocumentoCompartilhado() == null) {
            documentoCompartilhamentoList.setProcesso(processoVO.getProcesso());
            processoVO.setQtdDocumentoCompartilhado(documentoCompartilhamentoList.getResultCount());
        }
        return processoVO.getQtdDocumentoCompartilhado();
    }
    
    public boolean isDocumentoNaoPossuiAssinatura(Documento documento) {
    	List<AssinaturaDocumento> listaAssinaturaDocumento = assinaturaDocumentoManager.listAssinaturaDocumentoByDocumento(documento);
    	return listaAssinaturaDocumento != null && listaAssinaturaDocumento.size() == 0;
    }

    public DataList<Documento> getActiveBean() {
        return documentoCompartilhado ? documentoCompartilhamentoList : documentoList;
    }

    public void setPastaList(List<Pasta> pastaList) {
        this.pastaList = pastaList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        setInstance(pastaManager.find(id));
    }

    public String getTableTitle() {
        return getInstance() != null ? getInstance().getNome()
                : documentoCompartilhado ? nomePastaDocumentosCompartilhados : "";
    }

    public String getNomePasta(Pasta pasta) {
		return pastaManager.getNomePasta(pasta, documentoProcessoAction.getDocumentoFilter(), !documentoProcessoAction.podeUsuarioVerHistorico());
    }

    public String getNomePastaConfigurarCompartilhamento(Pasta pasta) {
        return String.format(msgConfigurarCompartilhamento, pasta.getNome());
    }

    public String getNomeDocumentoConfigurarCompartilhamento(Documento documento) {
        return String.format(msgConfigurarCompartilhamentoDoc, documento.getDescricao());
    }

    public Map<Integer, PastaRestricaoBean> getRestricoes() {
        return restricoes;
    }

    private void setRestricoes(Map<Integer, PastaRestricaoBean> restricoes) {
        this.restricoes = restricoes;
    }
    
    public Boolean isShowGrid() {
        return showGrid;
    }
    
    public void setShowGrid(Boolean showGrid) {
        this.showGrid = showGrid;
    }

    public List<PastaCompartilhamentoProcessoVO> getProcessoPastaCompList() {
        return processoPastaCompList;
    }

    public boolean isDocumentoCompartilhado() {
        return documentoCompartilhado;
    }

    public void setDocumentoCompartilhado(boolean documentoCompartilhado) {
        this.documentoCompartilhado = documentoCompartilhado;
        documentoProcessoAction.setDocumentoCompartilhado(documentoCompartilhado);
    }

    public boolean isPastaCompartilhada() {
        return pastaCompartilhada;
    }

    public void setPastaCompartilhada(boolean pastaCompartilhada) {
        this.pastaCompartilhada = pastaCompartilhada;
    }
}
