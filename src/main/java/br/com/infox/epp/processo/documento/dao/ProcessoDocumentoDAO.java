package br.com.infox.epp.processo.documento.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.ID_JDBPM_TASK_PARAM;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_ANEXOS_PUBLICOS_USUARIO_LOGADO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.NEXT_SEQUENCIAL;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.PARAM_PROCESSO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.PARAM_TIPO_NUMERACAO;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.USUARIO_PARAM;
import static br.com.infox.epp.processo.documento.query.ProcessoDocumentoQuery.LIST_PROCESSO_DOCUMENTO_BY_PROCESSO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.search.BooleanQuery.TooManyClauses;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.documento.type.TipoNumeracaoEnum;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.sigilo.service.SigiloDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.hibernate.session.SessionAssistant;
import br.com.infox.index.SimpleQueryParser;

@Name(ProcessoDocumentoDAO.NAME)
@AutoCreate
public class ProcessoDocumentoDAO extends DAO<ProcessoDocumento> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "processoDocumentoDAO";
    private static final Log LOG = Logging.getLog(ProcessoDocumentoDAO.class);

    @In
    private SessionAssistant sessionAssistant;
    @In
    private SigiloDocumentoService sigiloDocumentoService;

    public Integer getNextSequencial(Processo processo) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_PROCESSO, processo);
        parameters.put(PARAM_TIPO_NUMERACAO, TipoNumeracaoEnum.S);
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

    protected FullTextEntityManager getFullTextEntityManager() {
        return (FullTextEntityManager) super.getEntityManager();
    }
    
    public List<ProcessoDocumento> getListProcessoDocumentoByProcesso(Processo processo){
    	Map<String, Object> params = new HashMap<>(1);
    	params.put(PARAM_PROCESSO, processo);
    	return getNamedResultList(LIST_PROCESSO_DOCUMENTO_BY_PROCESSO, params);
    }

    @SuppressWarnings(UNCHECKED)
    public List<ProcessoDocumento> pesquisar(String searchPattern) {
        Session session = sessionAssistant.getSession();
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        List<ProcessoDocumento> ret = new ArrayList<ProcessoDocumento>();
        SimpleQueryParser parser = new SimpleQueryParser(new BrazilianAnalyzer(Version.LUCENE_36), "texto");
        Query luceneQuery;
        try {
            luceneQuery = parser.parse(searchPattern);
        } catch (TooManyClauses e) {
            LOG.warn("", e);
            return Collections.emptyList();
        }
        FullTextQuery hibernateQuery = fullTextSession.createFullTextQuery(luceneQuery, ProcessoDocumento.class);
        List<ProcessoDocumento> temp = hibernateQuery.list();
        for (ProcessoDocumento documento : temp) {
            if (documento.getAnexo()) {
                ret.add(documento);
            }
        }
        UsuarioLogin usuarioLogado = Authenticator.getUsuarioLogado();
        for (ProcessoDocumento documento : ret) {
            
            if (!sigiloDocumentoService.possuiPermissao(documento, usuarioLogado)){
                ret.remove(documento);
            }
        }
        return ret;
    }
}
