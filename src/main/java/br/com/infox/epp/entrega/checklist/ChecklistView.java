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
import org.primefaces.model.LazyDataModel;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.home.MovimentarController;
import br.com.infox.epp.processo.marcador.MarcadorSearch;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.variable.Taskpage;
import br.com.infox.ibpm.variable.TaskpageParameter;

@Named
@ViewScoped
@Taskpage(name = "checklist", description = "checklist.description")
public class ChecklistView implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String PARAMETER_PASTA = "pastaChecklist";

    @Inject
    private ChecklistService checklistService;
    @Inject
    private ChecklistSearch checklistSearch;
    @Inject
    private MovimentarController movimentarController;
    @Inject
    private TaskInstanceHome taskInstanceHome;
    @Inject
    private PastaManager pastaManager;
    @Inject
    private MarcadorSearch marcadorSearch;

    @TaskpageParameter(name = PARAMETER_PASTA, description = "Pasta a ser considerada no checklist")
    private Pasta pasta;

    // Controle geral
    private Processo processo;
    private boolean hasPasta;

    // Controle dos filtros
    private SelectItem[] classificacoesDocumento;
    private SelectItem[] situacoes;
    private SelectItem[] situacoesCompletas;

    // Controle do CheckList
    private Checklist checklist;
    private ChecklistDocLazyDataModel documentoList;
    private UsuarioLogin usuarioLogado;
    private String message;
    private ChecklistSituacao situacaoBloco;

    @PostConstruct
    private void init() {
        processo = movimentarController.getProcesso();
        pasta = retrievePasta();
        if (pasta == null) {
            hasPasta = false;
        } else {
            hasPasta = true;
            message = (String) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("checklistMessage");
            checklist = checklistService.getByProcessoPasta(processo, pasta);
            documentoList = new ChecklistDocLazyDataModel(checklist);
        }
        usuarioLogado = Authenticator.getUsuarioLogado();
    }

    /**
     * Tenta recuperar a Pasta baseado no Parâmetro {@link ChecklistView.PARAMETER_CHECKLIST_ENTREGA}
     * Caso não encontre, é tentado através da variável preenchida pelo listener
     * @return Pasta, caso encontre, null caso contrário.
     */
    private Pasta retrievePasta() {
        Object nomePasta = taskInstanceHome.getCurrentTaskInstance().getVariable(PARAMETER_PASTA);
        if (nomePasta == null) {
            message = "Não foi possível recuperar o parâmetro com o nome da pasta.";
            return null;
        } else {
            Pasta pasta = pastaManager.getPastaByNome((String) nomePasta, processo);
            if (pasta == null) {
                message = "Não foi possível encontrar uma pasta com o nome " + nomePasta + " neste processo.";
            }
            return pasta;
        }
    }
    
    public List<String> autoCompleteMarcadores(String query) {
        return marcadorSearch.listByPastaAndCodigo(pasta.getId(), query.toUpperCase(), documentoList.getCodigosMarcadores());
    }

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

    public void endTask() {
        if (checklistSearch.hasItemSemSituacao(checklist)) {
            FacesMessages.instance().add("Todos os documentos devem ter a situação informada.");
        } else {
            TaskInstanceHome.instance().end(TaskInstanceHome.instance().getName());
        }
    }

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

    public SelectItem[] getClassificacoesDocumento() {
        if (classificacoesDocumento == null) {
            List<ClassificacaoDocumento> classificacoes = checklistSearch.listClassificacaoDocumento(checklist);
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

    public ChecklistSituacao[] getChecklistSituacaoOptions() {
        return ChecklistSituacao.getValues();
    }

    public boolean isHasPasta() {
        return hasPasta;
    }

    public LazyDataModel<ChecklistDoc> getDocumentoList() {
        return documentoList;
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
