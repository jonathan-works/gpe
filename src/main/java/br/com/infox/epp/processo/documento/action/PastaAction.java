package br.com.infox.epp.processo.documento.action;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIOutput;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.partes.entity.ParticipanteProcesso;
import br.com.infox.epp.processo.partes.manager.ParticipanteProcessoManager;
import br.com.infox.seam.util.ComponentUtil;

@Name(PastaAction.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
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
    @In
    private ParticipanteProcessoManager participanteProcessoManager;
    @In
    private DocumentoList documentoList;

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
        setVisivelExterno(false);
        setVisivelNaoParticipante(false);
        setRemovivel(true);
        setSistema(false);
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
            pastaManager.persist(getInstance());
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

    public void remove(Pasta pasta) {
        try {
            if (pastaManager == null) {
                pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
            }
            pastaManager.remove(pasta);
            newInstance();
            FacesMessages.instance().add(Severity.INFO,
                    "Pasta removida com sucesso.");
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
        if (!pasta.getRemovivel() || !canEdit(pasta)) {
            return false;
        }
        List<Documento> documentoList = pasta.getDocumentosList();
        return (documentoList == null || documentoList.isEmpty());
    }

    public Boolean canEdit(Pasta pasta) {
        return pasta != null && !pasta.getSistema();
    }

    public Boolean canSee(Pasta pasta) {
        if (!pasta.getVisivelExterno())
            return false;
        if (pasta.getVisivelExterno() && pasta.getVisivelNaoParticipante())
            return true;
        UsuarioLogin usuario = (UsuarioLogin) Contexts.getSessionContext().get(
                "usuarioLogado");
        if (usuario == null || usuario.getPessoaFisica() == null)
            return false;
        PessoaFisica pessoaFisica = usuario.getPessoaFisica();
        ParticipanteProcesso participante = participanteProcessoManager
                .getParticipanteProcessoByPessoaProcesso(pessoaFisica,
                        pasta.getProcesso());
        return participante != null && participante.getAtivo()
                && pessoaFisica.equals(participante.getPessoa());
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

    public Boolean getVisivelNaoParticipante() {
        return getInstance().getVisivelNaoParticipante();
    }

    public void setVisivelNaoParticipante(Boolean visivelNaoParticipante) {
        getInstance().setVisivelNaoParticipante(visivelNaoParticipante);
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
        this.processo = processo.getProcessoRoot();
        try {
            this.pastaList = pastaManager.getByProcesso(processo.getProcessoRoot());
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
    	if (documentoList.getEntity().getClassificacaoDocumento() != null) {
    		return pastaManager.getNomePasta(pasta, documentoList.getEntity().getClassificacaoDocumento());
    	}
    	return pastaManager.getNomePasta(pasta);
    }
}

