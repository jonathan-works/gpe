package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.ArrayList;
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

@Name(PastaAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public class PastaAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pastaAction";

    @In
    private PastaManager pastaManager;
    @In
    private ActionMessagesService actionMessagesService;
    @In
    private DocumentoManager documentoManager;
    @In
    private DocumentoList documentoList;
    @In
    private DocumentoProcessoAction documentoProcessoAction;
    @In
    private PastaRestricaoManager pastaRestricaoManager;

    private Processo processo;
    private List<Pasta> pastaList = new ArrayList<>();
    private Pasta instance;
    private Integer id;
    private Map<Integer, PastaRestricaoBean> restricoes;
    private Boolean showGrid = false;

    @Create
    public void create() {
        newInstance();
    }

    public void newInstance() {
        setInstance(new Pasta());
        setRestricoes(new HashMap<Integer, PastaRestricaoBean>());
    }

    public void selectPasta(Pasta pasta) {
        documentoList.selectPasta(pasta);
        setInstance(pasta);
        setShowGrid(true);
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
    
    public List<Pasta> getPastaListComRestricaoEscrita() {
        List<Pasta> pastasRestritas= new ArrayList<>();
        for (Pasta pasta : pastaList) {
            if (canWrite(pasta)) {
                pastasRestritas.add(pasta);
            }
        }
        return pastasRestritas;
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
		return pastaManager.getNomePasta(pasta, documentoProcessoAction.getDocumentoFilter());
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
}