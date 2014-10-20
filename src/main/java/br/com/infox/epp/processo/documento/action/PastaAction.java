package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.List;

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

@Name(PastaAction.NAME)
@Scope(ScopeType.CONVERSATION)
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
    
    private Integer idPasta;
    private Integer idProcesso;
    private Processo processo;
    private String nome;
    private Boolean visivelExterno;
    private Boolean removivel;
    private List<Pasta> pastaList;

    @Create
    public void create() {
        setVisivelExterno(true);
        setRemovivel(true);
    }
    
    public void addPasta() {
        Pasta pasta = new Pasta();
        pasta.setNome(nome);
        pasta.setVisivelExterno(visivelExterno);
        pasta.setProcesso(processo);
        pasta.setRemovivel(removivel);
        try {
            pastaManager.persist(pasta);
            setPastaList(pastaManager.getByProcesso(processo));
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    public void associaDocumento(DropEvent evt) {
        Object od = evt.getDragValue();
        Object op = evt.getDropValue();
        if (od instanceof Documento && op instanceof Pasta) {
            Documento doc = (Documento) od;
            Pasta pasta = (Pasta) op;
            doc.setPasta(pasta);
            try {
                documentoManager.update(doc);
            } catch (DAOException e) {
                actionMessagesService.handleDAOException(e);
            }
        }
    }
    
    public Integer getIdPasta() {
        return idPasta;
    }

    public void setIdPasta(Integer idPasta) {
        this.idPasta = idPasta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getVisivelExterno() {
        return visivelExterno;
    }

    public void setVisivelExterno(Boolean visivelExterno) {
        this.visivelExterno = visivelExterno;
    }

    public Boolean getRemovivel() {
        return removivel;
    }

    public void setRemovivel(Boolean removivel) {
        this.removivel = removivel;
    }

    public Integer getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Integer idProcesso) {
        this.idProcesso = idProcesso;
        Processo processo = processoManager.find(idProcesso);
        if (processo != null) {
            setProcesso(processo);
        }
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

}
