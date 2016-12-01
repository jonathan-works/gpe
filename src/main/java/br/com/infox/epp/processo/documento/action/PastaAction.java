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
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.list.DocumentoList;
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
    private PastaRestricaoManager pastaRestricaoManager;
    @Inject
    private PastaCompartilhamentoSearch pastaCompartilhamentoSearch;
    @Inject
    private PastaCompartilhamentoService pastaCompartilhamentoService;
    @Inject
    private PastaCompartilhamentoView pastaCompartilhamentoView;

    private DocumentoProcessoAction documentoProcessoAction = ComponentUtil.getComponent(DocumentoProcessoAction.NAME);
    private DocumentoList documentoList = ComponentUtil.getComponent(DocumentoList.NAME);

    private LogProvider LOG = Logging.getLogProvider(PastaAction.class);

    private Processo processo;
    private List<Pasta> pastaList = new ArrayList<>();
    private Pasta instance;
    private Integer id;
    private Map<Integer, PastaRestricaoBean> restricoes;
    private Boolean showGrid = false;

    private String msgConfigurarCompartilhamento;
    private Map<Integer, Boolean> possuiCompartilhamentoMap = new HashMap<>();
    private List<Pasta> pastaCompartilhadaList = new ArrayList<>(0);
    private List<PastaCompartilhamentoProcessoVO> processoPastaCompList;
    private String msgRemoverCompartilhamento;
    private Pasta compartilhamentoToRemove;

    @PostConstruct
    public void create() {
        newInstance();
        InfoxMessages infoxMessages = InfoxMessages.getInstance();
        msgConfigurarCompartilhamento = infoxMessages.get("pasta.compartilhamento.msgConfigurar");
        msgRemoverCompartilhamento = infoxMessages.get("pasta.compartilhamento.remover.msg");
    }

    public void newInstance() {
        setInstance(new Pasta());
        setRestricoes(new HashMap<Integer, PastaRestricaoBean>());
    }

    public void selectPasta(Pasta pasta) {
        documentoList.selectPasta(pasta);
        setInstance(pasta);
        setShowGrid(true);
        for (Documento documento : documentoList.list(15)) {
            documentoProcessoAction.deveMostrarCadeado(documento);
        }
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
        PastaRestricaoBean restricaoDaPasta = restricoes.get(pasta.getId());
        return restricaoDaPasta != null && restricaoDaPasta.getDelete();
    }
    
    public Boolean canDeleteFromInstance() {
        return canDelete(getInstance());
    }
    
    public Boolean canLogicDelete(Pasta pasta) {
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

            loadPastasCompartilhadas(processo, usuario, localizacao, papel);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    private void loadPastasCompartilhadas(Processo processo, UsuarioLogin usuario, Localizacao localizacao, Papel papel) {
        this.pastaCompartilhadaList = pastaCompartilhamentoSearch.listPastasCompartilhadas(processo.getProcessoRoot());
        this.restricoes.putAll(loadRestricoesPastasCompartilhadas(usuario, localizacao, papel));
        preencherVOPastasCompartilhadas();
    }

    private Map<Integer, PastaRestricaoBean> loadRestricoesPastasCompartilhadas(UsuarioLogin usuario, Localizacao localizacao, Papel papel) {
        Map<Integer, PastaRestricaoBean> map = pastaRestricaoManager.loadRestricoes(pastaCompartilhadaList, usuario, localizacao, papel);
        for (PastaRestricaoBean restricaoBean : map.values()) {
            restricaoBean.setDelete(false);
            restricaoBean.setLogicDelete(false);
            restricaoBean.setWrite(false);
        }
        return map;
    }

    private void preencherVOPastasCompartilhadas() {
        processoPastaCompList = new ArrayList<>(0);
        Map<String, PastaCompartilhamentoProcessoVO> map = new HashMap<>();
        for (Pasta pasta : pastaCompartilhadaList) {
            if (canSee(pasta)) {
                String numeroProcesso = pasta.getProcesso().getProcessoRoot().getNumeroProcesso();
                PastaCompartilhamentoProcessoVO processoVO = map.get(numeroProcesso);
                if (processoVO != null) {
                    processoVO.addPasta(pasta);
                } else {
                    processoVO = new PastaCompartilhamentoProcessoVO(numeroProcesso, pasta);
                    processoPastaCompList.add(processoVO);
                    map.put(numeroProcesso, processoVO);
                }
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

    public String getMsgRemoverCompartilhamento() {
        return compartilhamentoToRemove == null ? ""
                : String.format(msgRemoverCompartilhamento, compartilhamentoToRemove.getNome(), processo.getNumeroProcesso());
    }

    public void removerCompartilhamento() {
        try {
            pastaCompartilhamentoService.removerCompartilhamento(compartilhamentoToRemove, processo);
            loadPastasCompartilhadas(processo, Authenticator.getUsuarioLogado(), Authenticator.getLocalizacaoAtual(), Authenticator.getPapelAtual());
            if (getInstance().equals(compartilhamentoToRemove)) {
                setShowGrid(false);
            }
            FacesMessages.instance().add("Compartilhamento removido com sucesso");
        } catch (Exception e) {
            FacesMessages.instance().add("Falha ao tentar remover compartilhamento. Favor tentar novamente.");
            LOG.error("pastaAction.removerCompartilhamento.", e);
        }
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

    public String getNomePasta(Pasta pasta) {
		return pastaManager.getNomePasta(pasta, documentoProcessoAction.getDocumentoFilter(), !documentoProcessoAction.podeUsuarioVerHistorico());
    }

    public String getNomePastaConfigurarCompartilhamento(Pasta pasta) {
        return String.format(msgConfigurarCompartilhamento, pasta.getNome());
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
}