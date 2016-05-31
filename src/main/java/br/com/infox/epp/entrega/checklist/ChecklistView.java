package br.com.infox.epp.entrega.checklist;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.variable.Taskpage;
import br.com.infox.ibpm.variable.TaskpageParameter;

@Named
@ViewScoped
@Taskpage(name = "checklist", description = "checklist.description")
public class ChecklistView implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_PASTA_CHECKLIST = "pastaChecklist";
    private static final String PARAMETER_CHECKLIST_ENTREGA = "checklistEntrega";

    @Inject
    private ChecklistService checklistService;
    @Inject
    private ChecklistSearch checklistSearch;

    // Parameters
    @TaskpageParameter(name = PARAMETER_CHECKLIST_ENTREGA, type = "Entrega", description = "checklist.parameter.entrega.description")
    private Entrega entrega;
    // TODO verificar a necessidade da pasta como parâmetro
    //      talvez seja útil para, encontrado o processo através da entrega, olhar uma pasta específica daquele processo?
    @TaskpageParameter(name = PARAMETER_PASTA_CHECKLIST, description = "checklist.parameter.pasta.description")
    private Pasta pasta;

    // TODO verificar controladores
    // Controle geral
    private TaskInstance taskInstance;
    private boolean hasPrestacao; // TODO verificar a utilidade deste controlador

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

    // FIXME ajustar inicialização do checklist
    // TODO fazer verificação de documentos novos
    @PostConstruct
    private void init() {
        entrega = retieveEntrega();
        if (entrega == null) {
            message = "Não foi possível encontrar uma Entrega de Documentos associada a este processo.";
        } else {
            message = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("checklistMessage");
            
        }
//        prestacaoContas = prestacaoContasDao.getByProcesso(processo.getIdProcesso());
//        setHasPrestacao(prestacaoContas != null);
//        if (isHasPrestacao()) {
//            checklist = checklistPCSearch.getByPrestacaoContas(prestacaoContas.getId());
//            if (checklist == null) {
//                checklist = checklistService.initCheckList(prestacaoContas);
//            }
//            documentoList = new ChecklistDocLazyDataModel(checklist);
//        }
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

    // TODO verificar método
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

 // TODO verificar método
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

 // TODO verificar método
    public void endTask() {
        if (checklistSearch.hasItemSemSituacao(checklist)) {
            FacesMessages.instance().add("Todos os documentos devem ter a situação informada.");
        } else {
            TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
        }
    }

 // TODO verificar método
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

    // FIXME ajustar método que vai em documentoPCDao
//    public SelectItem[] getClassificacoesDocumento() {
//        if (classificacoesDocumento == null) {
//            List<ClassificacaoDocumento> classificacoes = documentoPCDao.getClassificacoesDocumentoPCListByPrestacaoContas(prestacaoContas);
//            classificacoesDocumento = new SelectItem[classificacoes.size()];
//            for (int i = 0; i < classificacoes.size(); i++) {
//                ClassificacaoDocumento classificacaoDocumento = classificacoes.get(i);
//                classificacoesDocumento[i] = new SelectItem(classificacaoDocumento.getId(), classificacaoDocumento.toString());
//            }
//        }
//        return classificacoesDocumento;
//    }

 // TODO verificar método
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

    public boolean isHasPrestacao() {
        return hasPrestacao;
    }

    public void setHasPrestacao(boolean hasPrestacao) {
        this.hasPrestacao = hasPrestacao;
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
