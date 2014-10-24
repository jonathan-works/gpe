package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(PastaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class PastaAction implements Serializable, ActionListener {

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
    
    private Processo processo;
    private List<Pasta> pastaList;
    private Pasta instance;
    private Integer id;

    @Create
    public void create() {
        newInstance();
    }
    
    public void newInstance() {
        setInstance(new Pasta());
        setVisivelExterno(true);
        setRemovivel(true);
    }
    
    public void persist() {
        try {
            getInstance().setProcesso(processo);
            pastaManager.persist(getInstance());
            setPastaList(pastaManager.getByProcesso(processo));
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void update() {
        try {
            Pasta pasta = pastaManager.find(getId());
            pasta.setNome(getNome());
            pasta.setVisivelExterno(getVisivelExterno());
            pastaManager.update(pasta);
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void remove(Pasta pasta) {
        try {
            if (pastaManager == null) {
                pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
            }
            pastaManager.remove(pasta);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public Boolean canRemove(Pasta pasta) {
        if (pasta == null) {
            return false;
        }
        if (!pasta.getRemovivel()) {
            return false;
        }
        List<Documento> documentoList = pasta.getDocumentosList();
        return (documentoList == null || documentoList.isEmpty());
    }

    @Override
    public void processAction(ActionEvent event)
            throws AbortProcessingException {
        Map<String, Object> attributes = event.getComponent().getAttributes();
        Object o = attributes.get("pastaToSelect");
        if (o instanceof Pasta) {
            setInstance((Pasta) o);
            return;
        }
        o = attributes.get("pastaToRemove");
        if (o instanceof Pasta) {
            remove((Pasta) o); 
        }
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

    public Boolean getVisivelExterno() {
        return getInstance().getVisivelExterno();
    }

    public void setVisivelExterno(Boolean visivelExterno) {
        this.getInstance().setVisivelExterno(visivelExterno);
    }

    public Boolean getRemovivel() {
        return getInstance().getRemovivel();
    }

    public void setRemovivel(Boolean removivel) {
        this.getInstance().setRemovivel(removivel);
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
        try {
            this.pastaList = pastaManager.getByProcesso(processo);
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

}
