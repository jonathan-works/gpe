package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.epp.processo.entity.Processo;

@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChecklistVariableService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ChecklistSearch checklistSearch;
    @Inject
    private PastaManager pastaManager;

    private Processo retrieveProcessoFromExecutionContext() {
        ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
        Integer idProcesso = (Integer) executionContext.getContextInstance().getVariable("processo");
        Processo processo = EntityManagerProducer.getEntityManager().find(Processo.class, idProcesso);
        return processo;
    }

    public Boolean existeItemNaoConforme(String nomePasta) {
        Processo processo = retrieveProcessoFromExecutionContext();
        if (processo == null || processo.getIdJbpm() == null) {
            return false;
        }
        Pasta pasta = pastaManager.getPastaByNome(nomePasta, processo);
        if (pasta == null || pasta.getId() == null) {
            return false;
        }
        Checklist cl = checklistSearch.getByIdProcessoIdPasta(processo.getIdProcesso(), pasta.getId());
        return cl == null ? false : checklistSearch.hasItemNaoConforme(cl);
    }

    public String listBySituacao(String nomePasta, String codigoSituacao) {
        Processo processo = retrieveProcessoFromExecutionContext();
        Pasta pasta = pastaManager.getPastaByNome(nomePasta, processo);
        if (pasta == null || pasta.getId() == null) return "";
        return listBySituacao(processo.getIdProcesso(), pasta.getId(), codigoSituacao);
    }

    public String listBySituacao(Integer idProcesso, Integer idPasta, String codigoSituacao) {
        ChecklistSituacao situacao = ChecklistSituacao.valueOf(codigoSituacao);
        if (situacao == null) return "";
        Checklist checklist = checklistSearch.getByIdProcessoIdPasta(idProcesso, idPasta);
        if (checklist == null) {
            return "";
        }
        List<ChecklistDoc> clDocList = checklistSearch.getChecklistDocByChecklistSituacao(checklist, situacao);
        if (clDocList == null || clDocList.isEmpty()) {
            return "";
        }
        String response = "<table border=\"1\" style=\"border-collapse: collapse; width: 100%\">";
        response += "<thead>";
        response += "<th>Classificação de Documento</th>";
        response += "<th>Incluído por</th>";
        response += "<th>Motivo</th>";
        response += "</thead><tbody>"; 
        for (ChecklistDoc clDoc : clDocList) {
            response += "<tr>";
            response += "<td style=\"text-align: center;\">" + clDoc.getDocumento().getClassificacaoDocumento().getDescricao() + "</td>";
            response += "<td style=\"text-align: center;\">" + clDoc.getDocumento().getUsuarioInclusao().getNomeUsuario() + "</td>";
            response += "<td>" + (clDoc.getComentario() == null ? "" : clDoc.getComentario()) + "</td>";
            response += "</tr>";
        }
        response += "</tbody></table>";
        return response;
    }
}
