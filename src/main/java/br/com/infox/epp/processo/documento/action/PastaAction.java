package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.bean.PastaRestricaoBean;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.documento.manager.PastaRestricaoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(PastaAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public class PastaAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaAction";

    @In
    private ProcessoManager processoManager;
    @In
    private PastaManager pastaManager;
    @In
    private ActionMessagesService actionMessagesService;
    @In
    private DocumentoManager documentoManager;
    @In
    private ParticipanteProcessoManager participanteProcessoManager;
    @In
    private DocumentoList documentoList;
    @In
    private DocumentoProcessoAction documentoProcessoAction;
    @In
    private PastaRestricaoManager pastaRestricaoManager;

    private Processo processo;
    private List<Pasta> pastaList;
    private Pasta instance;
    private Integer id;
    private Map<Integer, PastaRestricaoBean> restricoes;

    @Create
    public void create() {
        newInstance();
    }

    public void newInstance() {
        setInstance(new Pasta());
        setVisivelExterno(false);
        setVisivelNaoParticipante(false);
        setRemovivel(true);
        setSistema(false);
        setRestricoes(new HashMap<Integer, PastaRestricaoBean>());
    }

    public void persist() {
        try {
            String nome = getNome();
            for (Pasta pasta : pastaList) {
                if (nome.equals(pasta.getNome())) {
                    FacesMessages.instance().add(Severity.INFO,
                            "Já existe pasta com este nome.");
                    return;
                }
            }
            if (!getVisivelExterno() && getVisivelNaoParticipante()) {
                setVisivelNaoParticipante(false);
            }
            getInstance().setProcesso(processo);
            setSistema(false);
            getInstance().setEditavel(Boolean.TRUE);
            getInstance().setRemovivel(Boolean.TRUE);
            pastaManager.persistWithDefault(getInstance());
            setPastaList(pastaManager.getByProcesso(processo));
            newInstance();
            FacesMessages.instance().add(StatusMessage.Severity.ERROR,
                    "Pasta adicionada com sucesso.");
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    public void update() {
        try {
            Pasta pasta = pastaManager.find(getId());
            pasta.setNome(getNome());
            pasta.setVisivelExterno(getVisivelExterno());
            pasta.setVisivelNaoParticipante(getVisivelNaoParticipante());
            if (!getVisivelExterno() && getVisivelNaoParticipante()) {
                setVisivelNaoParticipante(false);
            }
            pastaManager.update(pasta);
            newInstance();
            FacesMessages.instance().add(Severity.INFO,
                    "Pasta atualizada com sucesso.");
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    // TODO mais um método que precisa migrar de action
    public void remove(Pasta pasta) {
        try {
            if (pastaManager == null) {
                pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
            }
            documentoList.checkPastaToRemove(pasta);
            pastaManager.remove(pasta);
            newInstance();
            setPastaList(pastaManager.getByProcesso(processo.getProcessoRoot()));
            FacesMessages.instance().add(Severity.INFO,
                    "Pasta removida com sucesso.");
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    public void selectPasta(Pasta pasta) {
        documentoList.selectPasta(pasta);
        setInstance(pasta);
    }
    
    public void associaDocumento(DropEvent evt) {
        Object od = evt.getDragValue();
        Object op = evt.getDropValue();
        if (od instanceof Documento && op instanceof Pasta) {
            Documento doc = (Documento) od;
            Pasta pasta = (Pasta) op;
            Pasta pastaAnterior = doc.getPasta();
            doc.setPasta(pasta);
            try {
                documentoManager.update(doc);
                pastaManager.refresh(pasta);
                pastaManager.refresh(pastaAnterior);
            } catch (DAOException e) {
                actionMessagesService.handleDAOException(e);
            }
        }
    }

    // TODO marcado como Deprecated pois esta tela não mais remove pastas
    @Deprecated
    public Boolean canRemove(Pasta pasta) {
        if (!pasta.getRemovivel() || !canEdit(pasta)) {
            return false;
        }
        List<Documento> documentoList = pasta.getDocumentosList();
        return (documentoList == null || documentoList.isEmpty());
    }

    // TODO marcado como Deprecated pois esta tela não mais edita pastas
    @Deprecated
    public Boolean canEdit(Pasta pasta) {
        // TODO mudar para ver o campo editável
        return pasta != null && !pasta.getSistema();
    }

    public Boolean canSee(Pasta pasta) {
        return restricoes.get(pasta.getId()).getRead();
    }

    public Pasta getInstance() {
        return instance;
    }

    public void setInstance(Pasta pasta) {
        this.instance = pasta;
    }

    public String getNome() {
        return getInstance().getNome();
    }

    public void setNome(String nome) {
        this.getInstance().setNome(nome);
    }

    public String getDescricao(){
    	return getInstance().getDescricao();
    }
    
    public void setDescricao(String descricao){
    	this.getInstance().setDescricao(descricao);
    }
    
    public Boolean getVisivelExterno() {
        return getInstance().getVisivelExterno();
    }

    public void setVisivelExterno(Boolean visivelExterno) {
        this.getInstance().setVisivelExterno(visivelExterno);
    }

    public Boolean getVisivelNaoParticipante() {
        return getInstance().getVisivelNaoParticipante();
    }

    public void setVisivelNaoParticipante(Boolean visivelNaoParticipante) {
        getInstance().setVisivelNaoParticipante(visivelNaoParticipante);
    }

    @Deprecated
    public Boolean getRemovivel() {
        return getInstance().getRemovivel();
    }

    @Deprecated
    public void setRemovivel(Boolean removivel) {
        this.getInstance().setRemovivel(removivel);
    }

    public Processo getProcesso() {
        return processo;
    }

    // TODO verificar essa listagem de pastas, se já deve listar com restrições
    public void setProcesso(Processo processo) {
        this.processo = processo.getProcessoRoot();
        try {
            this.pastaList = pastaManager.getByProcesso(processo.getProcessoRoot());
            this.restricoes = pastaRestricaoManager.loadRestricoes(processo,
                    Authenticator.getUsuarioLogado(),
                    Authenticator.getLocalizacaoAtual(),
                    Authenticator.getPapelAtual());
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    public List<Pasta> getPastaList() {
        return pastaList;
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

    public Boolean getSistema() {
        return getInstance().getSistema();
    }

    public void setSistema(Boolean sistema) {
        getInstance().setSistema(sistema);
    }

    public String getNomePasta(Pasta pasta) {
		return pastaManager.getNomePasta(pasta, documentoProcessoAction.getDocumentoFilter());
    }

    public Map<Integer, PastaRestricaoBean> getRestricoes() {
        return restricoes;
    }

    public void setRestricoes(Map<Integer, PastaRestricaoBean> restricoes) {
        this.restricoes = restricoes;
    }

}

