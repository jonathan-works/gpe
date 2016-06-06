package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.entrega.documentos.Entrega;

@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChecklistVariableService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ChecklistSearch checklistSearch;

    public Boolean existeItemNaoConforme(Entrega entrega) {
        if (entrega == null || entrega.getId() == null) {
            return false;
        }
        Checklist cl = checklistSearch.getByIdEntrega(entrega.getId());
        return cl == null ? false : checklistSearch.hasItemNaoConforme(cl);
    }

    public String listBySituacao(Entrega entrega, String codigoSituacao) {
        return listBySituacao(entrega.getId(), codigoSituacao);
    }

    public String listBySituacao(Long idEntrega, String codigoSituacao) {
        ChecklistSituacao situacao = ChecklistSituacao.valueOf(codigoSituacao);
        if (situacao == null) return "";
        Checklist checklist = checklistSearch.getByIdEntrega(idEntrega);
        if (checklist == null) {
            return "";
        }
        List<ChecklistDoc> clDocList = checklistSearch.getChecklistDocByChecklistSituacao(checklist, situacao);
        if (clDocList == null || clDocList.isEmpty()) {
            return "";
        }
        String response = "<table border=\"1\" style=\"border-collapse: collapse;-\">";
        response += "<thead>";
        response += "<th>Classificação de Documento</th>";
        response += "<th>Incluído por</th>";
        response += "<th>Motivo</th>";
        response += "</thead><tbody>"; 
        for (ChecklistDoc clDoc : clDocList) {
            response += "<tr>";
            response += "<td>" + clDoc.getDocumento().getClassificacaoDocumento().getDescricao() + "</td>";
            response += "<td>" + clDoc.getDocumento().getUsuarioInclusao().getNomeUsuario() + "</td>";
            response += "<td>" + (clDoc.getComentario() == null ? "" : clDoc.getComentario()) + "</td>";
            response += "</tr>";
        }
        response += "</tbody></table>";
        return response;
    }
}
