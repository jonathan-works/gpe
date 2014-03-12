package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.ID_JDBPM_TASK_PARAM;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.PARAM_TIPO_PROCESSO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.USUARIO_PARAM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoDocumentoDAO.NAME)
@AutoCreate
public class ProcessoDocumentoDAO extends DAO<ProcessoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoDAO";

    public Integer getNextSequencial(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESSO, processo);
        parameters.put(PARAM_TIPO_PROCESSO, TipoNumeracaoEnum.S);
        return getNamedSingleResult(NEXT_SEQUENCIAL, parameters);
    }

    public String getModeloDocumentoByIdProcessoDocumento(
            Integer idProcessoDocumento) {
        ProcessoDocumento processoDocumento = find(idProcessoDocumento);
        if (processoDocumento != null) {
            return processoDocumento.getProcessoDocumentoBin().getModeloDocumento();
        }
        return null;
    }

    public List<ProcessoDocumento> getAnexosPublicos(long idJbpmTask) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_JDBPM_TASK_PARAM, idJbpmTask);
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        String query = LIST_ANEXOS_PUBLICOS;
        if (usuarioLogado != null) {
            parameters.put(USUARIO_PARAM, usuarioLogado);
            query = LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
        }
        return getNamedResultList(query, parameters);
    }
}
