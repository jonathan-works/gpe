package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.entrega.documentos.Entrega;

@Named
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChecklistVariableService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ChecklistSearch checklistSearch;

    // TODO testar existeItemNaoConforme
    public Boolean existeItemNaoConforme(Entrega entrega) {
        if (entrega == null || entrega.getId() == null) {
            return false;
        }
        Checklist cl = checklistSearch.getByIdEntrega(entrega.getId());
        return cl == null ? false : checklistSearch.hasItemNaoConforme(cl);
    }

    // TODO verificar método
    public String getNaoConformeList(Long idEntrega) {
        Checklist checklist = checklistSearch.getByIdEntrega(idEntrega);
        if (checklist == null) {
            return "";
        }
        List<ChecklistDoc> clDocList = checklistSearch.getChecklistDocByChecklistSituacao(checklist, ChecklistSituacao.NCO);
        String response = "";
        response += "<ul>";
        ClassificacaoDocumento classificacaoAnterior = null;
        // FIXME ajustar formatação de lista para tabela
        for (ChecklistDoc clDoc : clDocList) {
            ClassificacaoDocumento classificacaoAtual = clDoc.getDocumento().getClassificacaoDocumento();
            if (classificacaoAnterior == null) {
                response += "<li>Classificação de Documento: " + clDoc.getDocumento().getClassificacaoDocumento().getDescricao();
                response += "<ul><li>Documento: " + clDoc.getDocumento().getDescricao();
                response += "<ul><li>Comentário: " + (clDoc.getComentario() != null ? clDoc.getComentario() : "") +  "</li></ul></li></ul>";
            } else if (!classificacaoAtual.equals(classificacaoAnterior)) {
                response += "</li>";
                response += "<li>Classificação de Documento: " + clDoc.getDocumento().getClassificacaoDocumento().getDescricao();
                response += "<ul><li>Documento: " + clDoc.getDocumento().getDescricao() + ". ";
                response += "<ul><li>Comentário: " + (clDoc.getComentario() != null ? clDoc.getComentario() : "") +  "</li></ul></li></ul>";
            } else {
                response += "<ul><li>Documento: " + clDoc.getDocumento().getDescricao() + ". ";
                response += "<ul><li>Comentário: " + (clDoc.getComentario() != null ? clDoc.getComentario() : "") +  "</li></ul></li></ul>";
            }
            classificacaoAnterior = classificacaoAtual;
        }
        response += "</li></ul>";
        return response;
    }
}
