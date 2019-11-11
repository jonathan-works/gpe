package br.com.infox.epp.entrega.checklist;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import br.com.infox.epp.processo.marcador.Marcador;

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
    	return listBySituacao(nomePasta, codigoSituacao, true, false);
    }

    public String listBySituacao(String nomePasta, String codigoSituacao, boolean mostrarIncluidoPor, boolean mostrarMarcadores) {
        Processo processo = retrieveProcessoFromExecutionContext();
        Pasta pasta = pastaManager.getPastaByNome(nomePasta, processo);
        if (pasta == null || pasta.getId() == null) return "";
        return listBySituacao(processo.getIdProcesso(), pasta.getId(), codigoSituacao, mostrarIncluidoPor, mostrarMarcadores);
    }

    public String listBySituacao(Integer idProcesso, Integer idPasta, String codigoSituacao) {
    	return listBySituacao(idProcesso, idPasta, codigoSituacao, true, false);
    }

    public String listBySituacao(Integer idProcesso, Integer idPasta, String codigoSituacao, boolean mostrarIncluidoPor, boolean mostrarMarcadores) {
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
        String response = "<table border=\"1\" style=\"font-size: 12px; border-collapse: collapse; width: 100%\">";
        response += "<thead>";
        response += "<th>Classificação de Documento</th>";
        if(mostrarIncluidoPor) {
        	response += "<th>Incluído por</th>";
        }
        response += "<th>Motivo</th>";
        response += "</thead><tbody>"; 
        for (ChecklistDoc clDoc : clDocList) {
            response += "<tr>";
            if (mostrarMarcadores) {
                response += "<td style=\"text-align: center;\">" + clDoc.getDocumento().getClassificacaoDocumento().getDescricao();
                Set<Marcador> marcadores = clDoc.getDocumento().getDocumentoBin().getMarcadores();
                if (marcadores != null && !marcadores.isEmpty()) {
                    response += " (";
                    for (Iterator<Marcador> iterator = marcadores.iterator(); iterator.hasNext();) {
                        Marcador marcador = iterator.next();
                        response += iterator.hasNext() ? marcador.getCodigo() + ", " : marcador.getCodigo();
                    }
                    response += ")";
                }
                response += "</td>";
            } else {
                response += "<td style=\"text-align: center;\">" + clDoc.getDocumento().getClassificacaoDocumento().getDescricao() + "</td>";
            }
            if(mostrarIncluidoPor) {
            	response += "<td style=\"text-align: center;\">" + clDoc.getDocumento().getUsuarioInclusao().getNomeUsuario() + "</td>";
            }
            response += "<td>" + (clDoc.getComentario() == null ? "" : clDoc.getComentario()) + "</td>";
            response += "</tr>";
        }
        response += "</tbody></table>";
        return response;
    }
}
