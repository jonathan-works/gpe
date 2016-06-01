package br.com.infox.epp.entrega.checklist;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;

import org.jboss.seam.faces.FacesMessages;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.primefaces.model.LazyDataModel;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.dao.ClassificacaoDocumentoDAO;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.variable.Taskpage;
import br.com.infox.ibpm.variable.TaskpageParameter;

@Named
@ViewScoped
@Taskpage(name = "checklist", description = "checklist.description")
public class ChecklistView implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_CHECKLIST_ENTREGA = "checklistEntrega";

    @Inject
    private ChecklistService checklistService;
    @Inject
    private ChecklistSearch checklistSearch;
    @Inject
    private ClassificacaoDocumentoDAO classificacaoDocumentoDAO;

    // Parameters
    @TaskpageParameter(name = PARAMETER_CHECKLIST_ENTREGA, type = "Entrega", description = "checklist.parameter.entrega.description")
    private Entrega entrega;

    // Controle geral
    private TaskInstance taskInstance;
    private boolean hasEntrega;

    // TODO verificar controle dos filtros
    // Controle dos filtros
    private SelectItem[] classificacoesDocumento;
    private SelectItem[] situacoes;
    private SelectItem[] situacoesCompletas;

    // TODO verificar controle do checklist
    // Controle do CheckList
    private Checklist checklist;
    private LazyDataModel<ChecklistDoc> documentoList;
    private UsuarioLogin usuarioLogado;
    private String message;
    private ChecklistSituacao situacaoBloco;

    // TODO falta testar
    @PostConstruct
    private void init() {
        entrega = retieveEntrega();
        if (entrega == null) {
            hasEntrega = false;
            message = "Não foi possível encontrar uma Entrega de Documentos associada a este processo.";
        } else {
            hasEntrega = true;
            message = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("checklistMessage");
            checklist = checklistService.getByEntrega(entrega);
        }
        usuarioLogado = Authenticator.getUsuarioLogado();
    }

    /**
     * Tenta recuperar a Entrega baseado no processo em que está.
     * Primeiro é tentado através do Parâmetro {@link ChecklistView.PARAMETER_CHECKLIST_ENTREGA}
     * Caso não encontre, é tentado através da variável preenchida pelo listener
     * 
     * @return Entrega, caso encontre, null caso contrário.
     */
    private Entrega retieveEntrega() {
        taskInstance = TaskInstanceHome.instance().getCurrentTaskInstance();
        if (taskInstance.hasVariable(PARAMETER_CHECKLIST_ENTREGA)) {
            return (Entrega) taskInstance.getVariable(PARAMETER_CHECKLIST_ENTREGA);
        }
        String nameVariableEntrega = "nomeVariableEntrega"; // FIXME isso aqui precisa ser alterado após ser feita a refatoração do ObserverPrestacaoContas
        if (taskInstance.hasVariable(nameVariableEntrega)) {
            return (Entrega) taskInstance.getVariable(nameVariableEntrega);
        }
        return null;
    }

    // TODO testar onChangeSituacao
    public void onChangeSituacao(ChecklistDoc clDoc) {
        try {
            clDoc.setUsuarioAlteracao(usuarioLogado);
            clDoc.setDataAlteracao(new Date());
            if (ChecklistSituacao.CON.equals(clDoc.getSituacao()) || clDoc.getSituacao() == null) {
                clDoc.setComentario(null);
            }
            checklistService.update(clDoc);
            message = null;
        } catch (EJBException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("checklistMessage", "O item foi editado por outro usuário e por este motivo a página foi atualizada.");
                Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
                Object processo = requestMap.get("idProcesso");
                Object taskInstance = requestMap.get("idTaskInstance");
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("movimentar.seam?idProcesso=" + processo + "&idTaskInstance=" + taskInstance);
                } catch (IOException e1) {
                    FacesMessages.instance().add("Erro ao tentar atualizar a situação" + e.getMessage());
                }
            } else  {
                FacesMessages.instance().add("Erro ao tentar atualizar a situação" + e.getMessage());
            }
        }
    }

    // TODO testar onChangeComentario
    public void onChangeComentario(ChecklistDoc clDoc) {
        try {
            clDoc.setUsuarioAlteracao(usuarioLogado);
            clDoc.setDataAlteracao(new Date());
            checklistService.update(clDoc);
            message = null;
        } catch (EJBException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                FacesContext.getCurrentInstance().getExternalContext().getFlash().put("checklistMessage", "O item foi editado por outro usuário e por este motivo a página foi atualizada.");
                Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
                Object processo = requestMap.get("idProcesso");
                Object taskInstance = requestMap.get("idTaskInstance");
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("movimentar.seam?idProcesso=" + processo + "&idTaskInstance=" + taskInstance);
                } catch (IOException e1) {
                    FacesMessages.instance().add("Erro ao tentar atualizar a situação" + e.getMessage());
                }
            } else  {
                FacesMessages.instance().add("Erro ao tentar atualizar a situação" + e.getMessage());
            }
        }
    }

 // TODO testar finalização da taskpage checklist
    public void endTask() {
        if (checklistSearch.hasItemSemSituacao(checklist)) {
            FacesMessages.instance().add("Todos os documentos devem ter a situação informada.");
        } else {
            TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
        }
    }

 // TODO testar mudanças de situação em bloco
    @SuppressWarnings("unchecked")
    public void setBlockSituacao() {
        ArrayList<ChecklistDoc> list = (ArrayList<ChecklistDoc>) documentoList.getWrappedData();
        for (ChecklistDoc clDoc : list) {
            clDoc.setSituacao(situacaoBloco);
            onChangeSituacao(clDoc);
        }
        documentoList.setWrappedData(list);
        System.out.println(situacaoBloco.getLabel());
    }

    // TODO testar montagem da lista de classificações de documento
    public SelectItem[] getClassificacoesDocumento() {
        if (classificacoesDocumento == null) {
            List<ClassificacaoDocumento> classificacoes = classificacaoDocumentoDAO.getClassificacoesDocumentoByPasta(entrega.getPasta());
            classificacoesDocumento = new SelectItem[classificacoes.size()];
            for (int i = 0; i < classificacoes.size(); i++) {
                ClassificacaoDocumento classificacaoDocumento = classificacoes.get(i);
                classificacoesDocumento[i] = new SelectItem(classificacaoDocumento.getId(), classificacaoDocumento.toString());
            }
        }
        return classificacoesDocumento;
    }

    public SelectItem[] getSituacoes() {
        if (situacoes == null) {
            ChecklistSituacao[] values = ChecklistSituacao.getValues();
            situacoes = new SelectItem[values.length];
            for (int i = 0; i < values.length; i++) {
                ChecklistSituacao situacao = values[i];
                situacoes[i] = new SelectItem(situacao, situacao.getLabel());
            }
        }
        return situacoes;
    }

 // TODO verificar método
    public SelectItem[] getSituacoesCompletas() {
        if (situacoesCompletas == null) {
            ChecklistSituacao[] values = ChecklistSituacao.values();
            situacoesCompletas = new SelectItem[values.length];
            for (int i = 0; i < values.length; i++) {
                ChecklistSituacao situacao = values[i];
                situacoesCompletas[i] = new SelectItem(situacao, situacao.getLabel());
            }
        }
        return situacoesCompletas;
    }

 // TODO verificar método
    public ChecklistSituacao[] getChecklistSituacaoOptions() {
        return ChecklistSituacao.getValues();
    }

    public boolean isHasEntrega() {
        return hasEntrega;
    }

    public LazyDataModel<ChecklistDoc> getDocumentoList() {
        return documentoList;
    }

    public void setDocumentoList(LazyDataModel<ChecklistDoc> documentoList) {
        this.documentoList = documentoList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSituacoesCompletas(SelectItem[] situacoesCompletas) {
        this.situacoesCompletas = situacoesCompletas;
    }

    public ChecklistSituacao getSituacaoBloco() {
        return situacaoBloco;
    }

    public void setSituacaoBloco(ChecklistSituacao situacaoBloco) {
        this.situacaoBloco = situacaoBloco;
    }
}
